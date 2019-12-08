package com.sultanofcardio

import com.sultanofcardio.models.Attachment
import com.sultanofcardio.models.MailServer
import com.sultanofcardio.models.TEXT_PLAIN
import org.junit.Test
import java.io.File

class SendEmailTest {

    private val host = System.getenv("HOST")
    private val port = System.getenv("PORT")
    private val username = System.getenv("USERNAME")
    private val password = System.getenv("PASSWORD")
    private val recipient = System.getenv("RECIPIENT")
    private val ccRecipient = System.getenv("CC_RECIPIENT")
    private val bccRecipient = System.getenv("BCC_RECIPIENT")
    private val server: MailServer

    init {
        server = MailServer(host, port, username, password = password)
    }

    @Test
    fun send(){
        server.sendEmail(username, "Test", "Test from mailman", recipient)
    }

    @Test
    fun sendMultipleRecipients(){
        server.sendEmail(username, "Test", "Test from mailman", recipient, ccRecipient)
    }

    @Test
    fun sendMultipleRecipientsPostConfig(){
        server.sendEmail(username, "Test", "Test from mailman", recipient) {
            addRecipients(ccRecipient)
        }
    }

    @Test
    fun sendReplyTo(){
        server.sendEmail(username, "Test", "Test from mailman", recipient) {
            replyTo = "someone@example.com"
        }
    }

    @Test
    fun sendAltFrom(){
        server.sendEmail("someone@example.com", "Test", "Test from mailman", recipient)
    }

    @Test
    fun send2(){
        server.sendEmail(username, "Test", "Test 2 from mailman", recipient) {
            personalName = "Mailman"

            cc { +ccRecipient }

            bcc { +bccRecipient }

            addAttachment(Attachment("README.md", File("README.md").readBytes(), TEXT_PLAIN))
        }
    }
}
