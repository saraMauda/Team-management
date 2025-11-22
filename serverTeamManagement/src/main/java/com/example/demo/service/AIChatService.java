package com.example.demo.service;

import com.example.demo.model.Project;
import com.example.demo.model.Report;
import com.example.demo.model.Meeting;
import com.example.demo.model.Team;
import com.example.demo.model.TeamMember;
import com.example.demo.model.Users;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AIChatService {

    private final ChatClient chatClient;
    private final UsersRepository usersRepository;
    private final ProjectRepository projectRepository;
    private final ReportRepository reportRepository;
    private final MeetingRepository meetingRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public static String SYSTEM_INSTRUCTION = """
You are an AI assistant embedded inside a team-management system (TeamFlow).
Your only user is a Team Leader (team manager).

Language:
- Always answer in the same language used in the question (Hebrew or English).
- Tone: professional, clear, and friendly (not too formal, not slangy).

Scope:
- You only help with topics related to team management inside this system: projects, teams, employees, reports, meetings, tasks, attendance and productivity.
- If the user asks about something outside this scope (for example: general life questions, unrelated programming, random internet topics), politely say that this is outside your role.

Data and truth:
- You do NOT have direct access to the database.
- The surrounding Java application may send you a “system data” section inside the prompt with real information from the system.
- Treat that data as the single source of truth.
- Never invent numbers, dates, names or statuses that are not present in the given data.
- If there is no data section, answer in a generic way and say that no specific data was provided.

Answer style:
- Be concise and structured.
- Prefer: a short intro sentence + bullet list or simple structured text.
- When listing projects, meetings, reports or employees, prefer showing: id, name/title, status and important dates.
- If the question is vague or ambiguous, ask ONE short clarification question instead of guessing.

Destructive actions (updating / deleting / approving / changing data):
- Only the backend code can actually change data. You never really “execute” changes yourself.
- Do NOT say “I have updated/changed/deleted…”. Instead, explain what should be done, or suggest a short, explicit confirmation command that the user can send (for example: "confirm approve report 15" or "מאשרת מחיקת דוח 15") which the backend can then interpret.
- Always make clear what the consequences of a destructive action would be.

Goal:
- Help the Team Leader quickly understand what is going on in their team (projects, meetings, reports, reports by employees) and decide what to do next.
""";

    public AIChatService(ChatClient.Builder chatClient,
                         UsersRepository usersRepository,
                         ProjectRepository projectRepository,
                         ReportRepository reportRepository,
                         MeetingRepository meetingRepository,
                         TeamRepository teamRepository,
                         TeamMemberRepository teamMemberRepository) {

        this.chatClient = chatClient.build();
        this.usersRepository = usersRepository;
        this.projectRepository = projectRepository;
        this.reportRepository = reportRepository;
        this.meetingRepository = meetingRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    /**
     * Main entry point for the Team Leader assistant.
     * Uses Authentication (JWT) to understand who is asking,
     * pulls relevant data from the DB, and lets the model phrase the answer.
     */
    public String getResponse(String prompt, Authentication authentication) {

        // 1. מי המשתמש המחובר?
        Users currentUser = usersRepository.findByEmail(authentication.getName());
        Long leaderId = currentUser.getId();

        String normalized = prompt.toLowerCase();
        String systemData = null;

        // 2. Intent מאוד פשוט לפי מילות מפתח (עברית + אנגלית)
        if (containsAny(normalized, "project", "projects", "פרויק")) {
            systemData = buildProjectsSummary(leaderId);
        } else if (containsAny(normalized, "team", "צוות")) {
            systemData = buildTeamSummary(leaderId);
        } else if (containsAny(normalized, "report", "reports", "דוח", "דוחות")) {
            systemData = buildReportsSummary(leaderId);
        } else if (containsAny(normalized, "meeting", "meetings", "פגיש")) {
            systemData = buildMeetingsSummary(currentUser);
        }

        // 3. בונים את ההודעה למודל
        SystemMessage systemMessage = new SystemMessage(SYSTEM_INSTRUCTION);
        UserMessage userMessage;

        if (systemData != null) {
            // מעבירים למודל גם את השאלה וגם את הדאטה מהמערכת
            String combined = """
                    User query:
                    %s

                    System data (from the database, already filtered for this Team Leader):
                    %s

                    Please answer the user in the same language as the query (Hebrew or English),
                    in a professional and friendly tone.
                    """.formatted(prompt, systemData);

            userMessage = new UserMessage(combined);
        } else {
            // לא זיהינו intent → נותנים למודל לענות רק לפי השאלה
            userMessage = new UserMessage(prompt);
        }

        List<Message> messageList = List.of(systemMessage, userMessage);
        return chatClient.prompt().messages(messageList).call().content();
    }

    // ===========================
    //  Helpers – intent detection
    // ===========================

    private boolean containsAny(String text, String... tokens) {
        for (String t : tokens) {
            if (text.contains(t.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // ======================================
    //  Builders – לייצר טקסט מסודר מהדאטה
    // ======================================

    /**
     * פרויקטים שמנהל ראש הצוות המחובר.
     */
    private String buildProjectsSummary(Long leaderId) {
        List<Project> projects = projectRepository.findByProjectLeader_Id(leaderId);

        if (projects.isEmpty()) {
            return "No projects were found for this team leader.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Projects for leaderId=").append(leaderId).append(":\n");
        for (Project p : projects) {
            sb.append("- [ID=")
                    .append(p.getProjectId())
                    .append("] name=\"")
                    .append(p.getProjectName())
                    .append("\", status=")
                    .append(p.getProjectStatus())
                    .append(", start=")
                    .append(safeDate(p.getProjectStartDate()))
                    .append(", end=")
                    .append(safeDate(p.getProjectEndDate()))
                    .append("\n");
        }
        return sb.toString();
    }

    /**
     * צוותים + עובדים בצוות של המנהל (לפי TeamController).
     */
    private String buildTeamSummary(Long leaderId) {
        List<Team> teams = teamRepository.findByLeaderId(leaderId);

        if (teams.isEmpty()) {
            return "No teams were found for this team leader.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Teams for leaderId=").append(leaderId).append(":\n");
        for (Team t : teams) {
            sb.append("- Team ID=")
                    .append(t.getId())
                    .append("\n");

            List<TeamMember> members = teamMemberRepository.findByTeamId(t.getId());
            if (members.isEmpty()) {
                sb.append("  (no members)\n");
            } else {
                sb.append("  Members:\n");
                for (TeamMember m : members) {
                    Users u = m.getUser();
                    sb.append("  - [ID=")
                            .append(u.getId())
                            .append("] name=\"")
                            .append(u.getName())
                            .append("\", email=")
                            .append(u.getEmail())
                            .append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * דוחות של כל העובדים בצוות של המנהל (לפי הלוגיקה מ-ReportController.byLeader).
     */
    private String buildReportsSummary(Long leaderId) {
        // משתמשת באותה לוגיקה של ReportController.getReportsForLeader
        var teamMembers = reportRepository
                .findByReportEmployeeInProject_Project_ProjectLeader_Id(leaderId);

        // אם אין לך את המתודה הזאת, אפשר להעתיק מ-ReportController את הלוגיקה המלאה
        // עם EmployeeInProjectRepository ולהרכיב כאן List<Report> ידנית.

        if (teamMembers == null || teamMembers.isEmpty()) {
            return "No reports were found for this team leader.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Reports for leaderId=").append(leaderId).append(":\n");
        for (Report r : teamMembers) {
            sb.append("- [ID=")
                    .append(r.getReportId())
                    .append("] title=\"")
                    .append(r.getReportTitle())
                    .append("\", status=")
                    .append(r.getReportStatus())
                    .append(", date=")
                    .append(safeDate(r.getReportDate()))
                    .append("\n");
        }
        return sb.toString();
    }

    /**
     * פגישות של העובד/מנהל המחובר (לפי MeetingController.getMyMeetings).
     */
    private String buildMeetingsSummary(Users user) {
        // כאן את יכולה להשתמש ב-EmployeeInProjectRepository ו-MeetingRepository
        // כמו ב-MeetingController.getMyMeetings – אני משאיר לך מקום להשלים,
        // כדי שלא אמציא לך חתיכת קוד שלא תואמת בדיוק למודלים שלך.

        // כרגע נחזיר טקסט כללי, ואת יכולה להחליף למימוש מלא:
        return "Meeting summary is not fully implemented yet in AIChatService. " +
                "You can copy the logic from MeetingController.getMyMeetings into this method.";
    }

    private String safeDate(LocalDate date) {
        return (date == null) ? "N/A" : date.toString();
    }

    private String safeDate(LocalDateTime dt) {
        return (dt == null) ? "N/A" : dt.toString();
    }
}
