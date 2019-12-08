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
        server = MailServer(host, port, username, password = password, personalName = "Mailman")
    }


    @Test
    fun send(){
        server.sendEmail("Test", "Test from mailman", recipient)
    }

    @Test
    fun send2(){
        server.sendEmail("Test", "Test 2 from mailman", recipient) {
            cc { +ccRecipient }

            bcc { +bccRecipient }

            addAttachment(Attachment("README.md", File("README.md").readBytes(), TEXT_PLAIN))
        }
    }
}
