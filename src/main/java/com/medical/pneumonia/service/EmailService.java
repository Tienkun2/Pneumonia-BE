package com.medical.pneumonia.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

  JavaMailSender mailSender;

  @Value("${application.frontend.url:http://localhost:3000}")
  @lombok.experimental.NonFinal
  String frontendUrl;

  @Value("${application.mail.from:support@pneumonia.com}")
  @lombok.experimental.NonFinal
  String fromEmail;

  @Async
  public void sendActivationEmail(String to, String username, String token) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(fromEmail);
      helper.setTo(to);
      helper.setSubject("Kích hoạt tài khoản Pneumonia Medical System");

      String activationLink = frontendUrl + "/auth/activate?token=" + token;
      String content = buildActivationTemplate(username, activationLink);

      helper.setText(content, true);
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Failed to send activation email", e);
    }
  }

  private String buildActivationTemplate(String username, String activationLink) {
    return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
        </head>

        <body style="margin:0;padding:0;background:#eef3f8;font-family:Arial,Helvetica,sans-serif;">

          <table width="100%%" cellpadding="0" cellspacing="0" style="padding:30px 0;">
            <tr>
              <td align="center">

                <table width="600" cellpadding="0" cellspacing="0"
                  style="border-radius:10px;overflow:hidden;
                         box-shadow:0 4px 12px rgba(0,0,0,0.08);">

                  <!-- HEADER -->
                  <tr>
                    <td align="center" style="background:#0f4c81;padding:24px;color:white;">

                      <h2 style="margin:0;font-weight:600;">
                        Pneumonia Medical System
                      </h2>

                      <p style="margin:8px 0 0;font-size:14px;opacity:0.9;">
                        Hệ thống hỗ trợ chẩn đoán viêm phổi
                      </p>

                    </td>
                  </tr>

                  <!-- BODY -->
                  <tr>
                    <td style="background:white;padding:30px;color:#333;font-size:15px;line-height:1.6;">

                      <p>Xin chào <b>%s</b>,</p>

                      <p>
                        Cảm ơn bạn đã đăng ký tài khoản tại hệ thống <b>Pneumonia Medical</b>.
                      </p>

                      <p>
                        Vui lòng nhấn vào nút bên dưới để kích hoạt tài khoản của bạn:
                      </p>

                      <!-- BUTTON -->
                      <p style="text-align:center;margin:30px 0;">
                        <a href="%s"
                           style="display:inline-block;
                                  background:#1e88e5;
                                  color:#ffffff;
                                  font-weight:600;
                                  padding:14px 28px;
                                  border-radius:6px;
                                  text-decoration:none;">
                          Kích hoạt tài khoản
                        </a>
                      </p>

                      <p>
                        <b>Lưu ý:</b> Liên kết này sẽ hết hạn sau <b>24 giờ</b>.
                      </p>

                      <p>
                        Nếu bạn không thực hiện đăng ký, vui lòng bỏ qua email này.
                      </p>

                    </td>
                  </tr>

                  <!-- FOOTER -->
                  <tr>
                    <td style="background:#f4f8fb;padding:20px;
                               text-align:center;color:#666;
                               font-size:13px;">

                      <p style="margin:0;">
                        Liên hệ hỗ trợ:
                        <a href="mailto:support@pneumonia.com"
                           style="color:#1e88e5;text-decoration:none;">
                           support@pneumonia.com
                        </a>
                      </p>

                      <p style="margin-top:10px;">
                        © 2026 Pneumonia Medical System. All rights reserved.
                      </p>

                    </td>
                  </tr>

                </table>

              </td>
            </tr>
          </table>

        </body>
        </html>
        """
        .formatted(username, activationLink);
  }
}
