package com.example.swimming.etc.utils

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

// 이메일 보내기
class UtilSendEmail : Authenticator() {
    private var session: Session? = null

    init {
        val props = Properties()
        props.setProperty("mail.transport.protocol", "smtp")
        props.setProperty("mail.host", "smtp.gmail.com")
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.port"] = "465"
        props["mail.smtp.socketFactory.port"] = "465"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.socketFactory.fallback"] = "false"
        props.setProperty("mail.smtp.quitwait", "false")

        session = Session.getDefaultInstance(props, this)
    }

    override fun getPasswordAuthentication(): PasswordAuthentication? { // 구글 이메일 아이디/ 비밀번호 설정
        return PasswordAuthentication("swimapp1491@gmail.com", "@swim995")
    }

    @Synchronized
    @Throws(Exception::class)
    fun sendMail(title: String?, contents: String, destination: String) {
        val message = MimeMessage(session)
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destination))
        message.subject = title
        message.setText(contents)

        Transport.send(message) //메시지 전달
    }
}