@file:Suppress("MemberVisibilityCanBePrivate")

package com.sultanofcardio.models

import java.util.*

/**
 * An email message
 */
class Email(val subject: String, val body: String, vararg recipients: String) {

    val recipients = mutableSetOf(*recipients)
    val cc = mutableSetOf<String>()
    var bcc = mutableSetOf<String>()
    var properties = Properties()
    var attachments = mutableListOf<Attachment>()
    var noreply = false

    var contentType: String
        get() = properties.getOrDefault("contentType", TEXT_PLAIN) as String
        set(value) {
            properties["contentType"] = value
        }

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

    fun addProperty(name: String, property: Any): Email {
        properties[name] = property
        return this
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

    fun <T> property(name: String, defaultValue: T): T {
        val o: Any? = properties.getProperty(name)

        return try {
            o as T ?: defaultValue
        } catch (e: ClassCastException) {
            defaultValue
        }
    }
}
