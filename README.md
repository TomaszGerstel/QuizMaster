# Quizmaster

## Info

Application that allows learning and testing by completing ‘quizzes’, e.g. related to learning Java programming or any other topic. 
Currently, there are sample quizzes available related to learning Java, there is no possibility or need to log in to the service.

### Working app

https://quizmaster.tigerstel.xyz

### Technologies
- The ‘backend’ part of the application was written in Java using the Spring Framework;
- The ‘frontend’ was created using the Angular framework (also using Bootstrap in view) - frontend app is located in 'src/app/resources/frontend/quizmaster' folder;
- It uses the MongoDB non-relational database and the Liquibase tool for migration;
- When writing tests, apart form Java and JUnit, the Groovy language and the Spock and RestAssured libraries were used;

## Env variables

Create .env file in the root directory of the project and add the following properties:

- MONGO_USER=
- MONGO_PASS=
- MAIL_HOST=smtp.gmail.com
- MAIL_PORT=587
- MAIL_USERNAME=
- MAIL_PASSWORD=

## Using SMTP  Email Server locally

### create docker container for mailpit

docker run -d \
--name quizmaster-mailpit \
--restart unless-stopped \
-p 8025:8025 \
-p 1025:1025 \
axllent/mailpit:latest

### application.properties

mail:
    host: localhost
    port: 1025

### usage

Web UI: http://localhost:8025
SMTP: localhost:1025

## Quizmaster - Frontend

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 19.1.7.

Go to specific location of the project first.
e.g /src/main/resources/frontend/quizmaster

### Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

### Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

### Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

### Running unit tests

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

```bash
ng test
```

### Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

### Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
