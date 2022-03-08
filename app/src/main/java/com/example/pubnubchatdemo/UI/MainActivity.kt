package com.example.pubnubchatdemo.UI

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.pubnubchatdemo.Adapter.chatAdapter
import com.example.pubnubchatdemo.DataClass.ChatMessages
import com.example.pubnubchatdemo.R
import com.google.android.material.textfield.TextInputEditText
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNLogVerbosity
import com.pubnub.api.enums.PNReconnectionPolicy
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.history.PNFetchMessageItem
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.models.consumer.pubsub.PNSignalResult
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult
import com.pubnub.api.models.consumer.pubsub.objects.PNObjectEventResult
import java.util.*


class MainActivity : AppCompatActivity() {
    var uniqueId = "KELIS98"
    var btn: Button? = null
    var pubnub: PubNub? = null
    var adapter: chatAdapter? = null
    var recyclerView: RecyclerView? = null
    var editText: TextInputEditText? = null
    private val mMessages: ArrayList<ChatMessages> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //todo - Initialize Pubnub Chat
        implementPubNub()

        recyclerView = findViewById(R.id.recyclerview)
        btn = findViewById(R.id.send)
        editText = findViewById(R.id.text)
        adapter = chatAdapter(mMessages)
        recyclerView?.adapter = adapter
        btn!!.setOnClickListener {
            if (editText!!.text?.trim().toString().isNullOrEmpty()) {
                Toast.makeText(this, "PLease Enter Text", Toast.LENGTH_SHORT).show()
            } else {
                createSendMsg()
            }

        }


        //todo- get chat history
        getHistory()
        //todo - listener for arriving messages
        pubnub!!.addListener(object : SubscribeCallback() {
            override fun status(pubnub: PubNub, status: PNStatus) {
                println("Status category: ${status.category}")
                println("Status operation: ${status.operation}")
                println("Status error: ${status.error}")
            }

            override fun message(pubnub: PubNub, pnMessageResult: PNMessageResult) {
                println("Message: ${pnMessageResult}")
                println("Message ID: ${pubnub.instanceId}")
                println("Message payload: ${pnMessageResult.message}")
                println("Message channel: ${pnMessageResult.channel}")
                println("Message publisher: ${pnMessageResult.publisher}")
                println("Message timetoken: ${pnMessageResult.timetoken}")

                runOnUiThread {
                    mMessages.add(
                        ChatMessages(
                            pnMessageResult.message.toString(),
                            pnMessageResult.publisher.toString()
                        )
                    )
                    adapter!!.notifyDataSetChanged()
                    recyclerView?.scrollToPosition(mMessages.size - 1)
                }
            }

            override fun presence(pubnub: PubNub, pnPresenceEventResult: PNPresenceEventResult) {
                println("Presence: ${pnPresenceEventResult}")
                println("Presence event: ${pnPresenceEventResult.event}")
                println("Presence channel: ${pnPresenceEventResult.channel}")
                println("Presence uuid: ${pnPresenceEventResult.uuid}")
                println("Presence timetoken: ${pnPresenceEventResult.timetoken}")
                println("Presence occupancy: ${pnPresenceEventResult.occupancy}")
            }

            override fun messageAction(
                pubnub: PubNub,
                pnMessageActionResult: PNMessageActionResult
            ) {
                with(pnMessageActionResult.messageAction) {
                    println("Message action type: $type")
                    println("Message action value: $value")
                    println("Message action uuid: $uuid")
                    println("Message action actionTimetoken: $actionTimetoken")
                    println("Message action messageTimetoken: $messageTimetoken")
                }

                println("Message action subscription: ${pnMessageActionResult.subscription}")
                println("Message action channel: ${pnMessageActionResult.channel}")
                println("Message action timetoken: ${pnMessageActionResult.timetoken}")
            }

            override fun objects(pubnub: PubNub, objectEvent: PNObjectEventResult) {
                println("Object event channel: ${objectEvent.channel}")
                println("Object event publisher: ${objectEvent.publisher}")
                println("Object event subscription: ${objectEvent.subscription}")
                println("Object event timetoken: ${objectEvent.timetoken}")
                println("Object event userMetadata: ${objectEvent.userMetadata}")

                with(objectEvent.extractedMessage) {
                    println("Object event event: $event")
                    println("Object event source: $source")
                    println("Object event type: $type")
                    println("Object event version: $version")
                }
            }

        })


        Toast.makeText(this, "", Toast.LENGTH_SHORT).show()


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getHistory() {
        pubnub?.fetchMessages(
            channels = listOf("Channel_01")
        )?.async { result, status ->
            println("||||||||||||||||||| Entry" + result.toString())
            println("||||||||||||||||||| Entry" + status.toString())
//            result!!.messages.forEach {
//                println("||||||||||||||||||| Entry" + it.entry.toString())
//
//                mMessages.add(ChatMessages(it.entry.toString(), it.entry.toString()))
//                adapter?.notifyDataSetChanged()
//
//            }
//
//            recyclerView?.scrollToPosition(mMessages.size - 1)
            if (!status.error) {
                result!!.channels.forEach { (channel, messages) ->
                    println("Channel: $channel")
                    messages.forEach { messageItem: PNFetchMessageItem ->
                        println(messageItem.message) // actual message payload
                        println(messageItem.timetoken) // included by default
                        println(messageItem.uuid) // included by default
                        mMessages.add(
                            ChatMessages(messageItem.message.toString(),
                            messageItem.uuid.toString()
                        )
                        )
                    }
                    adapter?.notifyDataSetChanged()
                    recyclerView?.scrollToPosition(mMessages.size - 1)

//                    messages.forEach { messageItem: PNFetchMessageItem ->
////                        println(messageItem.message) // actual message payload
////                        println(messageItem.timetoken) // included by default
//                        messageItem.actions?.forEach { actionType, map ->
//                            println("Action type: $actionType")
//                            map.forEach { (actionValue, publishers) ->
//                                println("Action value: $actionValue")
//                                publishers.forEach { publisher ->
//                                    println("UUID: ${publisher.uuid}")
//                                    println("Timetoken: ${publisher.actionTimetoken}")
//                                }
//                            }
//                        }
//                    }
                }
            } else {
                // handle error
                status.exception?.printStackTrace()
            }

        }
    }

    private fun implementPubNub() {
        val config = PNConfiguration(uniqueId).apply {
            subscribeKey = "sub-c-3adb09b0-9bab-11ec-879a-86a1e6519840"
            publishKey = "pub-c-f511c054-ae12-4ca5-8cf3-1751e82190f6"
            uuid = uniqueId
            logVerbosity = PNLogVerbosity.BODY
            reconnectionPolicy = PNReconnectionPolicy.LINEAR
            maximumReconnectionRetries = 10
        }
        pubnub = PubNub(config)
//todo - Must Implement Subscribe to receive messages
        pubnub!!.subscribe(channels = listOf("Channel_01"))//List OF Channels

    }

    private fun createSendMsg() {
        //todo - for sending Messages
        this.pubnub!!.publish(channel = "Channel_01", message = editText?.text?.trim().toString())
            .async { result, status ->

                if (!status.error) {
                    println("Message timetoken: ${result!!.timetoken}")
                    editText?.setText("")
                    recyclerView?.scrollToPosition(mMessages.size - 1)
                } else {
                    status.exception!!.printStackTrace()
                }
            }



    }


}