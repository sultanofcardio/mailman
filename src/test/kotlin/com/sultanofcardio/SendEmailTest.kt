package com.sultanofcardio

import com.sultanofcardio.models.*
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
    fun sendBCCOnly(){
        server.sendEmail(username, "Test", "Test from mailman", "") {
            bcc {
                +bccRecipient
                +bccRecipient
            }
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

    @Test
    fun sendHTML(){
        server.sendEmail(username, "HTML Test", "Test HTML from mailman", recipient) {
            personalName = "Mailman"

            cc { +ccRecipient }

            bcc { +bccRecipient }

            contentType = TEXT_HTML

            addAttachment(Attachment("README.md", File("README.md").readBytes(), TEXT_PLAIN))
        }
    }

    @Test
    fun batchSend() {
        server.sendEmail(*Array(25) {
            Email(username, "Coroutines Batch HTML Test 15",
                "Batch HTML test from mailman <a href=\"https://kotlinlang.org/\">Kotlin</a> email library",
                recipient).apply {
                personalName = "Mailman"

                contentType = TEXT_HTML

                addAttachment(Attachment("README.md", File("README.md").readBytes(), TEXT_PLAIN))
            }
        })
    }

    @Test
    fun batchSend2() {

        val subject = "Coroutines Batch HTML Test 14"

        fun makeEmail(recipient: String, number: Int) = Email(username, subject, "Email $number", recipient)
            .apply {
                personalName = "Mailman"

                contentType = TEXT_HTML

                addAttachment(Attachment("README.md", File("README.md").readBytes(), TEXT_PLAIN))
            }

        val email1 = makeEmail(recipient, 1)
        val email2 = makeEmail("garbage", 2)
        val email3 = makeEmail(recipient, 3)
        val email4 = makeEmail(recipient, 4)
        val email5 = makeEmail("garbage", 5)

        println(server.sendEmail(email1, email2, email3, email4, email5).joinToString())
    }
}
