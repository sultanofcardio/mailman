@file:Suppress("MemberVisibilityCanBePrivate")

package com.sultanofcardio.models

import java.io.UnsupportedEncodingException
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource

class MailServer(val host: String, val port: String, val username: String, val properties: Properties = Properties(),
                 val password: String? = null) {
    val secure = password != null
    var init = false
    lateinit var session: Session

    private fun init() {
        session = if (secure) {
            val auth: Authenticator = object : Authenticator() {
                //override the getPasswordAuthentication method
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(username, password)
                }
            }
            Session.getDefaultInstance(properties, auth)
        } else {
            Session.getDefaultInstance(properties, null)
        }

        properties["mail.smtp.host"] = host
        properties["mail.smtp.port"] = port
        properties["name"] = username
        if (secure) {
            properties["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            properties["mail.smtp.auth"] = "true"
            properties["mail.smtp.starttls.enable"] = "true"
            properties["mail.smtp.socketFactory.port"] = port
        }


        init = true
    }

    fun sendEmail(email: Email): Boolean {

        if(email.recipients.size < 1) throw IllegalStateException("Email must have at least one recipient")

        if (!init) init()

        var success = false
        try {
            val msg = MimeMessage(session)

            //set message headers
            msg.addHeader("Content-type", email.contentType)
            msg.addHeader("format", email.format)
            msg.addHeader("Content-Transfer-Encoding", email.contentTransferEncoding)

            val fromAddr = InternetAddress(email.from, email.personalName ?: email.from)
            msg.setFrom(fromAddr)

            if (email.replyTo != null) msg.replyTo = InternetAddress.parse(email.replyTo, false)
            msg.setSubject(email.subject, email.charset)
            msg.sentDate = Date()

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.recipients.joinToString(","), false))
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(email.cc.joinToString(","), false))
            msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(email.bcc.joinToString(","), false))

            if (email.hasAttachment()) {
                var body = MimeBodyPart()
                var content: String = email.body
                body.setContent(content, email.contentType)
                val multipart: Multipart = MimeMultipart()
                multipart.addBodyPart(body)
                for (attachment in email.attachments) {
                    body = MimeBodyPart()
                    val source: DataSource =
                        ByteArrayDataSource(attachment.data, attachment.type)
                    body.dataHandler = DataHandler(source)
                    body.fileName = attachment.name
                    multipart.addBodyPart(body)
                }
                msg.setContent(multipart)
            } else {
                msg.setContent(email.body, email.contentType)
            }
            Transport.send(msg)
            success = true
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
        return success
    }

    fun sendEmail(from: String, subject: String, body: String, recipient: String, vararg otherRecipients: String,
                  configure: Email.() -> Unit = {}): Boolean {
        val email = Email(from, subject, body, recipient, *otherRecipients)
        configure(email)
        return sendEmail(email)
    }

}