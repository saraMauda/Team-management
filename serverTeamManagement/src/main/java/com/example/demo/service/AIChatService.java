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
                         EmployeeInProjectRepository employeeInProjectRepository,
                         TeamMemberRepository teamMemberRepository) {

        this.chatClient = chatClient.build();
        this.usersRepository = usersRepository;
        this.projectRepository = projectRepository;
        this.reportRepository = reportRepository;
        this.meetingRepository = meetingRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.employeeInProjectRepository=employeeInProjectRepository;
    }

    /**
     * Main entry point for the Team Leader assistant.
     * Uses Authentication (JWT) to understand who is asking,
     * pulls relevant data from the DB, and lets the model phrase the answer.
     */
    public String getResponse(String prompt, String conversationId, Authentication authentication) {

        // היסטוריה לפי שיחה
        List<Message> history = conversations.computeIfAbsent(conversationId, k -> new ArrayList<>());

        // system
        SystemMessage system = new SystemMessage(SYSTEM_INSTRUCTION);

        // הודעת המשתמש + system-data
        UserMessage userMessage = buildUserMessageWithSystemData(prompt, authentication);

        // בניית כל מה שנשלח למודל
        List<Message> fullConversation = new ArrayList<>();
        fullConversation.add(system);
        fullConversation.addAll(history);
        fullConversation.add(userMessage);

        // קריאה למודל
        var result = chatClient
                .prompt()
                .messages(fullConversation)
                .call();

        String aiText = result.content();

        // היסטוריה
        history.add(userMessage);
        history.add(new AssistantMessage(aiText));

        return aiText;
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

        for (Team team : teams) {

            List<TeamMember> members = teamMemberRepository.findByTeamId(team.getId());

            for (TeamMember tm : members) {
                Users user = tm.getUser();

                int projectCount = employeeInProjectRepository.countByUser_Id(user.getId());

                List<Report> userReports =
                        reportRepository.findByReportEmployeeInProject_Project_Leader_Id(leaderId).stream()
                                .filter(r -> r.getReportEmployeeInProject().getUser().getId() == user.getId())
                                .toList();

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

    private String buildReportsByEmployeeSummary(Long leaderId) {

        List<Report> reports =
                reportRepository.findByReportEmployeeInProject_Project_Leader_Id(leaderId);

        if (reports.isEmpty()) {
            return "ReportsByEmployee: none";
        }

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
        if (containsAny(normalized,
                "פנוי", "עמוס", "עומס",
                "free", "available", "load", "busy")) {

            systemData = buildEmployeeLoadSummary(leaderId);
        }

        else if (containsAny(normalized,
                "דוחות לפי עובד", "פעיל", "הכי פעיל",
                "activity", "report count", "reports per employee")) {

            systemData = buildReportsByEmployeeSummary(leaderId);
        }

        else if (containsAny(normalized,
                "מומח", "התמח", "skill", "skills",
                "expert", "expertise", "specialty")) {

            systemData = buildEmployeeSpecialtiesSummary(leaderId);
        }

        else if (containsAny(normalized,
                "מתאים", "הכי טוב", "הכי מתאים", "מומלץ",
                "fit", "suitable", "recommend", "best")) {

            String specialties = buildEmployeeSpecialtiesSummary(leaderId);
            String load = buildEmployeeAdvancedAnalysis(leaderId);
            systemData = specialties + "\n" + load;
        }

        else if (containsAny(normalized, "עובד", "employees", "employee")) {

            systemData = buildEmployeeFitSummary(leaderId);
        }

        else if (containsAny(normalized, "project", "projects", "פרויק")) {
            systemData = buildProjectsSummary(leaderId);
        } else if (containsAny(normalized, "team", "צוות")) {
            systemData = buildTeamSummary(leaderId);
        } else if (containsAny(normalized, "report", "reports", "דוח", "דוחות")) {
            systemData = buildReportsSummary(leaderId);
        } else if (containsAny(normalized, "meeting", "meetings", "פגיש")) {
            systemData = buildMeetingsSummary(currentUser);
        }

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
