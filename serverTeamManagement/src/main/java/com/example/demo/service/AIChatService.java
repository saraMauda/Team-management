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
Your only user is a Team Leader.

Language:
- Always answer in the same language used in the question (Hebrew or English).
- Tone: professional, clear, and friendly.

Scope:
- You ONLY assist with team-management topics: projects, teams, employees, reports, meetings, tasks, attendance and productivity.
- If the user asks about anything unrelated (weather, general knowledge, programming, personal matters), politely decline.

Data:
- You do NOT have access to the database.
- The application sends you a “system data” section. That is the ONLY source of truth.
- Never invent names, dates, numbers or statuses.
- If no system data is provided, say so.

ABSOLUTE FORMAT RULES (MANDATORY):
1. NO Markdown at all:
   No **bold**, no *, no _, no markup, no code blocks.
2. NO IDs unless the user explicitly asks.
3. ALL list items must be formatted in MULTI-LINE style.
4. Use ONLY these characters for structure:
   •  (bullet)
   →  (arrow for date ranges)
5. Every list item MUST follow this structure:

Meetings:
• {meetingTitle}
  Date: {date}
  Location: {location}
  Status: {status}

Projects:
• {projectName}
  Status: {status}
  Start: {startDate}
  End: {endDate}

Reports:
• {reportTitle}
  Status: {status}
  Date: {date}

Employees:
• {name}
  Email: {email}

REQUIRED BEHAVIOR:
- The FIRST LINE must contain ONLY the bullet and the title.
- Every following property must be placed on ITS OWN line.
- Each property line MUST begin with exactly two spaces.
- NEVER combine fields into the same line.
- NEVER return raw system data; always rewrite it cleanly.
- NEVER output lists in a single line.

Answer style:
- Short friendly intro sentence.
- Then multi-line formatted items.
- If unclear, ask ONE clarification question.

Destructive actions:
- The AI cannot perform real actions.
- Ask for explicit confirmation (e.g., “confirm approve report 12”).

Goal:
- Provide clear, structured, helpful multi-line summaries that look clean inside a chat interface.
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
            return "Projects: none";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Projects:\n");

        for (Project p : projects) {
            sb.append("• ").append(p.getProjectName()).append("\n")
                    .append("  Status: ").append(p.getProjectStatus()).append("\n")
                    .append("  Start: ").append(safeDate(p.getProjectStartDate())).append("\n")
                    .append("  End: ").append(safeDate(p.getProjectEndDate())).append("\n\n");
        }
        return sb.toString();
    }

    /**
     * צוותים + עובדים בצוות של המנהל (לפי TeamController).
     */
    private String buildTeamSummary(Long leaderId) {
        List<Team> teams = teamRepository.findByLeaderId(leaderId);

        if (teams.isEmpty()) {
            return "Team members: none";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Team members:\n");

        for (Team t : teams) {
            List<TeamMember> members = teamMemberRepository.findByTeamId(t.getId());

            for (TeamMember m : members) {
                Users u = m.getUser();
                sb.append("• ").append(u.getName()).append("\n")
                        .append("  Email: ").append(u.getEmail()).append("\n\n");
            }
        }

        return sb.toString();
    }

    /**
     * דוחות של כל העובדים בצוות של המנהל (לפי הלוגיקה מ-ReportController.byLeader).
     */
    private String buildReportsSummary(Long leaderId) {
        List<Report> reports = reportRepository
                .findByReportEmployeeInProject_Project_ProjectLeader_Id(leaderId);

        if (reports == null || reports.isEmpty()) {
            return "Reports: none";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Reports:\n");

        for (Report r : reports) {
            sb.append("• ").append(r.getReportTitle()).append("\n")
                    .append("  Status: ").append(r.getReportStatus()).append("\n")
                    .append("  Date: ").append(safeDate(r.getReportDate())).append("\n\n");
        }

        return sb.toString();
    }


    /**
     * פגישות של העובד/מנהל המחובר (לפי MeetingController.getMyMeetings).
     */
    private String buildMeetingsSummary(Users user) {

        List<Meeting> meetings =
                meetingRepository.findByApprovals_ApprovalEmployeeInProject_User_Id(user.getId());

        if (meetings == null || meetings.isEmpty()) {
            return "Meetings: none";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Meetings:\n");

        for (Meeting m : meetings) {
            sb.append("• ").append(m.getTitle()).append("\n")
                    .append("  Date: ").append(safeDate(m.getMeetingDate())).append("\n")
                    .append("  Location: ").append(
                            m.getMeetingLocation() == null ? "N/A" : m.getMeetingLocation()
                    ).append("\n")
                    .append("  Status: ").append(
                            m.getStatus() == null ? "N/A" : m.getStatus()
                    ).append("\n\n");
        }

        return sb.toString();
    }

    private String safeDate(LocalDate date) {
        return (date == null) ? "N/A" : date.toString();
    }

    private String safeDate(LocalDateTime dateTime) {
        return (dateTime == null) ? "N/A" : dateTime.toString();
    }
}
