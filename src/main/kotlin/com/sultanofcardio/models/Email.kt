@file:Suppress("MemberVisibilityCanBePrivate")

package com.sultanofcardio.models

/**
 * An email message
 */
class Email(var from: String, var subject: String, var body: String, recipient: String, vararg otherRecipients: String) {

    val recipients = mutableSetOf(recipient).apply { addAll(otherRecipients) }
    val cc = mutableSetOf<String>()
    var bcc = mutableSetOf<String>()
    var attachments = mutableListOf<Attachment>()
    var replyTo: String? = null
    var personalName: String? = null
    var contentType: String = TEXT_PLAIN

    var charset = "UTF-8"
    var format = "flowed"
    var contentTransferEncoding = "8bit"

    fun addRecipients(vararg recipients: String): Email {
        this.recipients.addAll(recipients)
        return this
    }

    fun addCC(vararg cc: String): Email {
        this.cc.addAll(cc)
        return this
    }

    fun addBCC(vararg bcc: String): Email {
        this.bcc.addAll(bcc)
        return this
    }

    fun addAttachment(attachment: Attachment): Email {
        attachments.add(attachment)
        return this
    }

    fun hasAttachment(): Boolean {
        return attachments.size > 0
    }

    fun cc(configure: CC.() -> Unit) {
        configure(CC(cc))
    }

    class CC(private val cc: MutableSet<String>) {
        operator fun String.unaryPlus() {
            cc.add(this)
        }
    }

    fun bcc(configure: BCC.() -> Unit) {
        configure(BCC(bcc))
    }

    class BCC(private val bcc: MutableSet<String>) {
        operator fun String.unaryPlus() {
            bcc.add(this)
        }
    }

    fun recipients(configure: Recipients.() -> Unit) {
        configure(Recipients(recipients))
    }

    class Recipients(private val recipients: MutableSet<String>) {
        operator fun String.unaryPlus() {
            recipients.add(this)
        }
    }
}
