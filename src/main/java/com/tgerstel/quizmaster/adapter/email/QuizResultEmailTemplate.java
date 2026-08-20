package com.tgerstel.quizmaster.adapter.email;

import com.tgerstel.quizmaster.domain.model.QuizAttempt;

public final class QuizResultEmailTemplate {

    private QuizResultEmailTemplate() {
    }

    public static String create(QuizAttempt quizAttempt) {

        String status = quizAttempt.getIsPassed()
                ? "PASSED"
                : "FAILED";

        String statusColor = quizAttempt.getIsPassed()
                ? "#198754"
                : "#dc3545";

        return """
                <!DOCTYPE html>
                <html lang="pl">
                <head>
                    <meta charset="UTF-8">
                    <title>Quizz result</title>
                </head>
                <body style="font-family: Arial, sans-serif; line-height: 1.5; color: #333;">

                    <h2>Quiz Master</h2>

                    <p>Your quiz attempt has been evaluated. Below are the details of your result:</p>

                    <h3>%s</h3>

                    <table style="border-collapse: collapse; margin: 20px 0;">
                        <tr>
                            <td style="padding: 8px 20px 8px 0;"><strong>Status:</strong></td>
                            <td style="padding: 8px; color: %s;">
                                <strong>%s</strong>
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 20px 8px 0;"><strong>Score:</strong></td>
                            <td style="padding: 8px;">%d / %d</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 20px 8px 0;"><strong>Percentage score:</strong></td>
                            <td style="padding: 8px;">%d%%</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 20px 8px 0;"><strong>Required to pass:</strong></td>
                            <td style="padding: 8px;">%d%%</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 20px 8px 0;"><strong>Time:</strong></td>
                            <td style="padding: 8px;">%s</td>
                        </tr>
                    </table>

                    <p>
                        Thanks for using Quiz Master.
                    </p>

                </body>
                </html>
                """.formatted(
                quizAttempt.getQuizName(),
                statusColor,
                status,
                quizAttempt.getCorrectAnswers(),
                quizAttempt.getQuestionsCount(),
                quizAttempt.getCorrectAnswers() * 100 / quizAttempt.getQuestionsCount(),
                quizAttempt.getPassRate(),
                formatDuration(quizAttempt.getQuizDurationSeconds())
        );
    }

    private static String formatDuration(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        return "%d min %02d s".formatted(minutes, remainingSeconds);
    }
}