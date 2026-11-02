

INSERT IGNORE INTO roles (id, name, category, description) VALUES
(1, 'Full Stack Java Developer', 'Web Development',
 'Builds complete web applications using Java for backend (Spring Boot) and React for frontend. Works on databases, APIs, and UI.'),

(2, 'Frontend Developer', 'Web Development',
 'Specializes in building user interfaces using HTML, CSS, JavaScript, and React. Focuses on user experience and design.'),

(3, 'Data Scientist', 'Data Science',
 'Analyzes large datasets to extract insights, builds ML models, and helps organizations make data-driven decisions.'),

(4, 'DevOps Engineer', 'DevOps',
 'Automates software delivery pipelines, manages cloud infrastructure, and ensures system reliability and scalability.');



INSERT IGNORE INTO skills (id, name, category, priority, description, estimated_hours, resource_url, role_id) VALUES


(1,  'Java',         'Programming Language', 1, 'Core programming language for backend development. Learn OOP, collections, and exception handling.', 80, 'https://www.javatpoint.com/java-tutorial', 1),
(2,  'Spring Boot',  'Framework',            1, 'Java framework for building REST APIs and web applications rapidly with minimal configuration.', 60, 'https://spring.io/guides', 1),
(3,  'MySQL',        'Database',             1, 'Relational database for storing application data. Learn SQL queries, joins, and indexing.', 40, 'https://www.w3schools.com/mysql/', 1),
(4,  'React',        'Framework',            1, 'JavaScript library for building interactive user interfaces with component-based architecture.', 60, 'https://react.dev/learn', 1),
(5,  'REST API',     'Concept',              1, 'Design and consume RESTful APIs. Understand HTTP methods, status codes, and JSON.', 20, 'https://restfulapi.net/', 1),
(6,  'HTML & CSS',   'Web Technology',       1, 'Foundation of web development. Structure and style web pages.', 30, 'https://www.w3schools.com/', 1),
(7,  'JavaScript',   'Programming Language', 1, 'Essential scripting language for web interactivity and React development.', 50, 'https://javascript.info/', 1),


(8,  'Git',          'Tool',                 2, 'Version control system. Learn branching, merging, and collaboration workflows.', 15, 'https://git-scm.com/doc', 1),
(9,  'Maven',        'Build Tool',           2, 'Build automation and dependency management tool for Java projects.', 10, 'https://maven.apache.org/guides/', 1),
(10, 'Hibernate/JPA','ORM Framework',        2, 'Maps Java objects to database tables, eliminating manual SQL for CRUD operations.', 25, 'https://hibernate.org/orm/documentation/', 1),
(11, 'Postman',      'Tool',                 2, 'API testing tool for testing REST endpoints during development.', 8, 'https://learning.postman.com/', 1),


(12, 'Docker',       'DevOps',               3, 'Containerization tool for packaging and running applications consistently across environments.', 20, 'https://docs.docker.com/get-started/', 1),
(13, 'AWS Basics',   'Cloud',                3, 'Cloud platform basics for deploying your application online.', 30, 'https://aws.amazon.com/getting-started/', 1);


INSERT IGNORE INTO skills (id, name, category, priority, description, estimated_hours, resource_url, role_id) VALUES
(14, 'HTML5',        'Web Technology',       1, 'Semantic HTML markup language to structure web content.', 20, 'https://www.w3schools.com/html/', 2),
(15, 'CSS3',         'Web Technology',       1, 'Styling language for layouts, animations, and responsive design.', 30, 'https://www.w3schools.com/css/', 2),
(16, 'JavaScript',   'Programming Language', 1, 'Core language for browser interactivity and dynamic content.', 60, 'https://javascript.info/', 2),
(17, 'React',        'Framework',            1, 'Most popular UI library. Learn components, hooks, and state management.', 60, 'https://react.dev/learn', 2),
(18, 'Tailwind CSS', 'CSS Framework',        2, 'Utility-first CSS framework for fast UI building.', 20, 'https://tailwindcss.com/docs', 2),
(19, 'Git',          'Tool',                 2, 'Version control for tracking changes and collaboration.', 15, 'https://git-scm.com/doc', 2),
(20, 'TypeScript',   'Programming Language', 2, 'Typed superset of JavaScript that catches errors early.', 30, 'https://www.typescriptlang.org/docs/', 2),
(21, 'Figma Basics', 'Design Tool',          3, 'Read design files and collaborate with UI/UX designers.', 10, 'https://help.figma.com/hc/en-us', 2);



INSERT IGNORE INTO skills (id, name, category, priority, description, estimated_hours, resource_url, role_id) VALUES
(22, 'Python',            'Programming Language', 1, 'Primary language for data science. Learn syntax, functions, and OOP.', 60, 'https://www.python.org/doc/', 3),
(23, 'Statistics & Math', 'Mathematics',          1, 'Probability, statistics, linear algebra - the foundation of ML algorithms.', 80, 'https://www.khanacademy.org/math/statistics-probability', 3),
(24, 'Pandas',            'Library',              1, 'Python library for data manipulation and analysis.', 30, 'https://pandas.pydata.org/docs/', 3),
(25, 'NumPy',             'Library',              1, 'Numerical computing library for arrays and mathematical operations.', 20, 'https://numpy.org/doc/', 3),
(26, 'Machine Learning',  'Concept',              1, 'Supervised, unsupervised learning algorithms using scikit-learn.', 80, 'https://scikit-learn.org/stable/tutorial/', 3),
(27, 'Data Visualization','Tool',                 2, 'Matplotlib and Seaborn for creating insightful charts and graphs.', 20, 'https://matplotlib.org/stable/tutorials/', 3),
(28, 'SQL',               'Database',             2, 'Query structured data from relational databases.', 25, 'https://www.w3schools.com/sql/', 3),
(29, 'Jupyter Notebook',  'Tool',                 2, 'Interactive environment for data exploration and analysis.', 10, 'https://jupyter.org/documentation', 3),
(30, 'Deep Learning',     'Advanced ML',          3, 'Neural networks using TensorFlow or PyTorch for complex problems.', 100, 'https://www.tensorflow.org/tutorials', 3);



INSERT IGNORE INTO skills (id, name, category, priority, description, estimated_hours, resource_url, role_id) VALUES
(31, 'Linux',         'Operating System',    1, 'Command line, file system, permissions, and shell scripting.', 40, 'https://linuxjourney.com/', 4),
(32, 'Git',           'Tool',                1, 'Advanced Git workflows, branching strategies, and CI/CD integration.', 20, 'https://git-scm.com/doc', 4),
(33, 'Docker',        'Containerization',    1, 'Containerize applications for consistent deployment across environments.', 35, 'https://docs.docker.com/', 4),
(34, 'CI/CD',         'Concept',             1, 'Automate testing and deployment pipelines using Jenkins or GitHub Actions.', 40, 'https://docs.github.com/en/actions', 4),
(35, 'AWS',           'Cloud Platform',      1, 'EC2, S3, RDS, Lambda - core AWS services for infrastructure.', 80, 'https://aws.amazon.com/getting-started/', 4),
(36, 'Kubernetes',    'Orchestration',       2, 'Container orchestration for scaling and managing Docker containers.', 60, 'https://kubernetes.io/docs/tutorials/', 4),
(37, 'Terraform',     'Infrastructure',      2, 'Infrastructure as Code for provisioning cloud resources.', 40, 'https://developer.hashicorp.com/terraform/tutorials', 4),
(38, 'Monitoring',    'Tool',                3, 'Prometheus and Grafana for system health monitoring and alerting.', 25, 'https://prometheus.io/docs/introduction/overview/', 4);
