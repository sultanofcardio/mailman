@file:Suppress("MemberVisibilityCanBePrivate")

package com.sultanofcardio.models

import kotlinx.coroutines.*
import java.io.UnsupportedEncodingException
import java.time.Instant
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource

class MailServer(
    host: String,
    port: String,
    val username: String,
    properties: Properties = Properties(),
    val password: String? = null
) {
    val secure = password != null
    var session: Session

    init {
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
    }

    fun sendEmail(vararg emails: Email): Array<Boolean> = runBlocking {
        emails.filter { it.recipients.size > 0 }
            .map { email ->
                GlobalScope.async {
                    val transport = session.transport
                    transport.connect()
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

                        msg.setRecipients(
                            Message.RecipientType.TO,
                            InternetAddress.parse(email.recipients.joinToString(","), false)
                        )
                        msg.setRecipients(
                            Message.RecipientType.CC,
                            InternetAddress.parse(email.cc.joinToString(","), false)
                        )
                        msg.setRecipients(
                            Message.RecipientType.BCC,
                            InternetAddress.parse(email.bcc.joinToString(","), false)
                        )

                        if (email.hasAttachment()) {
                            var body = MimeBodyPart()
                            val content: String = email.body
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
                        msg.saveChanges()
                        if (!transport.isConnected) throw IllegalStateException("Not connected")
                        transport.sendMessage(msg, msg.allRecipients)
                        true
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                        false
                    } catch (e: MessagingException) {
                        e.printStackTrace()
                        false
                    } finally {
                        if (transport.isConnected) transport.close()
                    }
                }
            }.map { it.await() }
            .toTypedArray()
    }

    val time: Instant get() = Instant.now()

    // To maintain backwards compatibility
    @Suppress("RemoveRedundantSpreadOperator")
    fun sendEmail(email: Email): Boolean = sendEmail(*arrayOf(email))[0]

    fun sendEmail(
        from: String, subject: String, body: String, recipient: String, vararg otherRecipients: String,
        configure: Email.() -> Unit = {}
    ): Boolean {
        val email = Email(from, subject, body, recipient, *otherRecipients)
        configure(email)
        return sendEmail(email)
    }

}