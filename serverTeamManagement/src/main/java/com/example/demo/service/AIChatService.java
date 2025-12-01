package com.example.demo.service;

import com.example.demo.model.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AIChatService {

    private final ChatClient chatClient;
    private final UsersRepository usersRepository;
    private final ProjectRepository projectRepository;
    private final ReportRepository reportRepository;
    private final MeetingRepository meetingRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final EmployeeInProjectRepository employeeInProjectRepository;
    private final Map<String, List<Message>> conversations = new ConcurrentHashMap<>();


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
                         EmployeeInProjectRepository employeeInProjectRepository ,
                         TeamMemberRepository teamMemberRepository) {

        this.chatClient = chatClient.build();
        this.usersRepository = usersRepository;
        this.projectRepository = projectRepository;
        this.reportRepository = reportRepository;
        this.meetingRepository = meetingRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.employeeInProjectRepository = employeeInProjectRepository;
    }

    /**
     * Main entry point for the Team Leader assistant.
     * Uses Authentication (JWT) to understand who is asking,
     * pulls relevant data from the DB, and lets the model phrase the answer.
     */
    public String getResponse(String prompt, String conversationId, Authentication authentication) {

        // 1. שליפת היסטוריה קיימת
        List<Message> history = conversations.computeIfAbsent(conversationId, k -> new ArrayList<>());

        // 2. יצירת SystemMessage (כמו שיש לך)
        SystemMessage system = new SystemMessage(SYSTEM_INSTRUCTION);

        // 3. בניית systemData והוספת UserMessage
        UserMessage userMessage = buildUserMessageWithSystemData(prompt, authentication);

        // 4. הוספת הודעת המשתמש להיסטוריה
        history.add(userMessage);

        // 5. בניית רשימת הודעות מלאה (system + history)
        List<Message> fullConversation = new ArrayList<>();
        fullConversation.add(system);
        fullConversation.addAll(history);

        // 6. קריאה ל־ChatClient
        String response = chatClient.prompt().messages(fullConversation).call().content();

        // 7. שמירת תגובת ה-AI בהיסטוריה
        history.add(new AssistantMessage(response));

        return response;
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
        List<Project> projects = projectRepository.findByLeader_Id(leaderId);

        if (projects.isEmpty()) {
            return "Projects: none";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Projects:\n");

        for (Project p : projects) {
            sb.append("• ").append(p.getName()).append("\n")
                    .append("  Status: ").append(p.getStatus()).append("\n")
                    .append("  Start: ").append(safeDate(p.getStartDate())).append("\n")
                    .append("  End: ").append(safeDate(p.getEndDate())).append("\n\n");
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
                .findByReportEmployeeInProject_Project_Leader_Id(leaderId);

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

    private String buildEmployeeFitSummary(Long leaderId) {

        List<Team> teams = teamRepository.findByLeaderId(leaderId);

        if (teams.isEmpty()) {
            return "Employees: none";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Employees:\n");

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

    private String buildEmployeeAdvancedAnalysis(Long leaderId) {

        List<Team> teams = teamRepository.findByLeaderId(leaderId);
        if (teams.isEmpty()) {
            return "Employees: none";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Employees:\n");

        // נעבור על הצוותים
        for (Team team : teams) {

            // שליפת כל העובדים
            List<TeamMember> members = teamMemberRepository.findByTeamId(team.getId());

            for (TeamMember tm : members) {
                Users user = tm.getUser();

                // כמות פרויקטים שבהם העובד משתתף
                int projectCount = employeeInProjectRepository.countByUser_Id(user.getId());


                // דוחות שעבד עליהם (רמז לתחום העיסוק)
                List<Report> userReports =
                        reportRepository.findByReportEmployeeInProject_Project_Leader_Id(leaderId).stream()
                                .filter(r -> r.getReportEmployeeInProject().getUser().getId() == user.getId())
                                .toList();

                // תאריכים אחרונים (פעילות)
                String lastReportDate = userReports.isEmpty()
                        ? "N/A"
                        : userReports.get(userReports.size() - 1).getReportDate().toString();

                sb.append("• ").append(user.getName()).append("\n")
                        .append("  Email: ").append(user.getEmail()).append("\n")
                        .append("  ActiveProjects: ").append(projectCount).append("\n")
                        .append("  LastReport: ").append(lastReportDate).append("\n")
                        .append("  Notes: fewer active projects means more availability\n\n");
            }
        }

        return sb.toString();
    }
    private String buildEmployeeLoadSummary(Long leaderId) {

        List<EmployeeInProject> members =
                employeeInProjectRepository.findByProject_Leader_Id(leaderId);

        if (members.isEmpty()) {
            return "Employees: none";
        }

        Map<Users, Integer> loadMap = new HashMap<>();

        for (EmployeeInProject eip : members) {
            Users u = eip.getUser();

            int count = employeeInProjectRepository.countByUser_Id(u.getId());

            loadMap.put(u, count);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Employees:\n");

        loadMap.forEach((user, load) -> {
            sb.append("• ").append(user.getName()).append("\n")
                    .append("  Email: ").append(user.getEmail()).append("\n")
                    .append("  CurrentProjects: ").append(load).append("\n\n");
        });

        return sb.toString();
    }
    private String buildEmployeeSpecialtiesSummary(Long leaderId) {

        List<EmployeeInProject> members =
                employeeInProjectRepository.findByProject_Leader_Id(leaderId);

        if (members.isEmpty()) {
            return "Employees: none";
        }

        Map<Users, String> specialties = new HashMap<>();

        for (EmployeeInProject eip : members) {
            Users user = eip.getUser();

            String specialty = eip.getRoleDescription();
            if (specialty == null || specialty.trim().isEmpty()) {
                specialty = "N/A";
            }

            specialties.put(user, specialty);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Employees:\n");

        specialties.forEach((user, specialty) -> {
            sb.append("• ").append(user.getName()).append("\n")
                    .append("  Email: ").append(user.getEmail()).append("\n")
                    .append("  Specialty: ").append(specialty).append("\n\n");
        });

        return sb.toString();
    }

    private String buildReportsByEmployeeSummary(Long leaderId) {

        List<Report> reports =
                reportRepository.findByReportEmployeeInProject_Project_Leader_Id(leaderId);

        if (reports.isEmpty()) {
            return "ReportsByEmployee: none";
        }

        // מיפוי עובד → רשימת דוחות
        Map<Users, List<Report>> map = new HashMap<>();

        for (Report r : reports) {
            Users u = r.getReportEmployeeInProject().getUser();

            map.computeIfAbsent(u, k -> new ArrayList<>()).add(r);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ReportsByEmployee:\n");

        for (Map.Entry<Users, List<Report>> entry : map.entrySet()) {
            Users user = entry.getKey();
            List<Report> employeeReports = entry.getValue();

            sb.append("• ").append(user.getName()).append("\n")
                    .append("  Email: ").append(user.getEmail()).append("\n");

            for (Report r : employeeReports) {
                sb.append("  → ").append(r.getReportTitle()).append("\n")
                        .append("     Date: ").append(safeDate(r.getReportDate())).append("\n")
                        .append("     Status: ").append(r.getReportStatus()).append("\n");
            }

            sb.append("\n");
        }

        return sb.toString();
    }



    private String safeDate(LocalDate date) {
        return (date == null) ? "N/A" : date.toString();
    }

    private String safeDate(LocalDateTime dateTime) {
        return (dateTime == null) ? "N/A" : dateTime.toString();
    }

    private UserMessage buildUserMessageWithSystemData(String prompt, Authentication authentication) {

        Users currentUser = usersRepository.findByEmail(authentication.getName());
        Long leaderId = currentUser.getId();

        String normalized = prompt.toLowerCase();
        String systemData = null;

        // ============================
        // A) שאלות על עומס / פנוי
        // ============================
        if (containsAny(normalized,
                "פנוי", "עמוס", "עומס",
                "free", "available", "load", "busy")) {

            systemData = buildEmployeeLoadSummary(leaderId);
        }

        else if (containsAny(normalized,
                "דוחות לפי עובד", "פעיל", "הכי פעיל", "activity", "report count", "reports per employee")) {

            systemData = buildReportsByEmployeeSummary(leaderId);
        }

        // ============================
        // B) שאלות על כישורים / מומחיות
        // ============================
        else if (containsAny(normalized,
                "מומח", "התמח", "skill", "skills",
                "expert", "expertise", "specialty")) {

            systemData = buildEmployeeSpecialtiesSummary(leaderId);
        }

        // ============================
        // C) שאלות על התאמה / מי הכי טוב
        // ============================
        else if (containsAny(normalized,
                "מתאים", "הכי טוב", "הכי מתאים", "מומלץ",
                "fit", "suitable", "recommend", "best")) {

            String specialties = buildEmployeeSpecialtiesSummary(leaderId);
            String load = buildEmployeeAdvancedAnalysis(leaderId);
            systemData = specialties + "\n" + load;
        }

        // ============================
        // D) שאלות כלליות על עובדים
        // ============================
        else if (containsAny(normalized, "עובד", "employees", "employee")) {

            systemData = buildEmployeeFitSummary(leaderId);
        }

        // ============================
        // E) שאר הדברים הרגילים
        // ============================
        else if (containsAny(normalized, "project", "projects", "פרויק")) {
            systemData = buildProjectsSummary(leaderId);
        }

        else if (containsAny(normalized, "team", "צוות")) {
            systemData = buildTeamSummary(leaderId);
        }

        else if (containsAny(normalized, "report", "reports", "דוח", "דוחות")) {
            systemData = buildReportsSummary(leaderId);
        }

        else if (containsAny(normalized, "meeting", "meetings", "פגיש")) {
            systemData = buildMeetingsSummary(currentUser);
        }

        // ============================
        // החזרת ההודעה
        // ============================
        if (systemData != null) {
            String combined = """
            User query:
            %s
            
            System data (from the database, already filtered for this Team Leader):
            %s
            
            Please answer the user in the same language as the query (Hebrew or English),
            in a professional and friendly tone.
            """.formatted(prompt, systemData);

            return new UserMessage(combined);
        }

        return new UserMessage(prompt);
    }


}
