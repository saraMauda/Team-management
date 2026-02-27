<h1 align="center">TeamFlow</h1>
<h3 align="center">Enterprise Team Management Platform</h3>

<hr/>

<h2>Overview</h2>
<p>
TeamFlow is a full-stack Team Management Platform designed to manage organizational
structure, user roles, and team operations using a secure and scalable architecture.
</p>

<p>
The system demonstrates strong backend engineering principles, secure authentication
implementation, structured frontend architecture, and automation-ready design.
It was developed with emphasis on clean code, separation of concerns, and production-oriented standards.
</p>

<hr/>

<h2>Core Capabilities</h2>

<h3>Role-Based Access Control</h3>

<ul>
  <li><strong>Admin</strong>
    <ul>
      <li>Full user lifecycle management</li>
      <li>Role assignment and permission control</li>
      <li>System-wide visibility</li>
    </ul>
  </li>

  <li><strong>Team Leader</strong>
    <ul>
      <li>Team member management</li>
      <li>Meeting creation and tracking</li>
      <li>Operational oversight</li>
    </ul>
  </li>

  <li><strong>Employee</strong>
    <ul>
      <li>Profile management</li>
      <li>Meeting participation</li>
      <li>Controlled access to team resources</li>
    </ul>
  </li>
</ul>

<p>
All permissions are enforced at backend level using Spring Security.
</p>

<hr/>

<h2>System Architecture</h2>

<p>
The platform follows a layered architecture:
</p>

<pre>
Controller → Service → Repository → Database
</pre>

<h3>Backend Design</h3>
<ul>
  <li>RESTful API</li>
  <li>DTO pattern for secure data transfer</li>
  <li>Service-layer business logic isolation</li>
  <li>Spring Data JPA abstraction</li>
  <li>Centralized exception handling</li>
  <li>JWT-based authentication</li>
  <li>BCrypt password encryption</li>
</ul>

<h3>Frontend Design</h3>
<ul>
  <li>Angular component-based architecture</li>
  <li>Route guards for role-based protection</li>
  <li>Modular API services</li>
  <li>Strong TypeScript typing</li>
  <li>Clear separation of concerns</li>
</ul>

<hr/>

<h2>Technology Stack</h2>

<h3>Backend</h3>
<ul>
  <li>Java</li>
  <li>Spring Boot</li>
  <li>Spring Security (JWT)</li>
  <li>Spring Data JPA</li>
  <li>Hibernate</li>
  <li>H2 In-Memory Database</li>
</ul>

<h3>Frontend</h3>
<ul>
  <li>Angular</li>
  <li>TypeScript</li>
  <li>HTML5</li>
  <li>CSS3</li>
</ul>

<h3>DevOps & Tooling</h3>
<ul>
  <li>Git / GitHub</li>
  <li>Postman</li>
  <li>Jenkins</li>
  <li>Automated API Testing</li>
  <li>Automated UI Testing</li>
</ul>

<hr/>

<h2>Database Design</h2>

<p>
The system uses H2 in-memory database for development and testing.
The architecture is designed for seamless migration to production-grade databases
such as MySQL or PostgreSQL.
</p>

<p>Main entities:</p>
<ul>
  <li>User</li>
  <li>Role</li>
  <li>Team</li>
  <li>Meeting</li>
  <li>Task</li>
</ul>

<hr/>

<h2>Security Implementation</h2>

<ul>
  <li>Stateless JWT authentication</li>
  <li>Role-based authorization</li>
  <li>BCrypt password hashing</li>
  <li>Protected REST endpoints</li>
  <li>Angular route guards</li>
</ul>

<p>
Unauthorized access is restricted at both backend and frontend layers.
</p>

<hr/>

<h2>Application Screenshots</h2>

<div style="display: flex; justify-content: center; gap: 20px;">
  <img src="screenshots/admin-dashboard.png" width="450"/>
  <img src="screenshots/admin users.png" width="450"/>
</div>

<div style="display: flex; justify-content: center; gap: 20px;">
  <img src="screenshots/admin reports.png" width="450"/>
  <img src="screenshots/teamLeader dashboard.png" width="450"/>
</div>

<div style="display: flex; justify-content: center; gap: 20px;">
  <img src="screenshots/meetings.png" width="450"/>
  <img src="screenshots/projects.png" width="450"/>
</div>

<div style="display: flex; justify-content: center; gap: 20px;">
  <img src="screenshots/teamLeader AI.png" width="450"/>
  <img src="screenshots/teamLeaderreport.png" width="450"/>
</div>

<div style="display: flex; justify-content: center; gap: 20px;">
  <img src="screenshots/employee add report.png" width="450"/>
  <img src="screenshots/ew=mployee dashboard.png" width="450"/>
</div>

<div style="display: flex; justify-content: center; gap: 20px;">
  <img src="screenshots/login.png" width="450"/>
</div>

<hr/>

<h2>Installation</h2>

<h3>Clone Repository</h3>

<pre>
git clone https://github.com/your-username/TeamFlow.git
cd TeamFlow
</pre>

<h3>Backend</h3>

<pre>
mvn spring-boot:run
</pre>

<p>Default: http://localhost:8080</p>
<p>H2 Console: http://localhost:8080/h2-console</p>

<h3>Frontend</h3>

<pre>
npm install
ng serve
</pre>

<p>Default: http://localhost:4200</p>

<hr/>

<h2>Engineering Highlights</h2>

<ul>
  <li>Clean layered architecture</li>
  <li>Strong separation of concerns</li>
  <li>Secure authentication flow</li>
  <li>Role-based authorization enforcement</li>
  <li>Automation-ready infrastructure</li>
  <li>CI integration compatible</li>
  <li>Production migration-ready design</li>
</ul>

<hr/>

<h2>Future Enhancements</h2>

<ul>
  <li>Migration to production-grade SQL database</li>
  <li>Docker containerization</li>
  <li>Cloud deployment</li>
  <li>Real-time notifications</li>
  <li>Advanced dashboard analytics</li>
</ul>

<hr/>

<p align="center">
Developed by Sara<br/>
Software Engineering Student
</p>
