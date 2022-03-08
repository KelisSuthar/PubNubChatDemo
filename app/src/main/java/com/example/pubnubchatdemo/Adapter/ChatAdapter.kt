package com.example.pubnubchatdemo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.pubnubchatdemo.DataClass.ChatMessages
import com.example.pubnubchatdemo.R
import java.util.ArrayList

class chatAdapter(var array: ArrayList<ChatMessages>)
    : RecyclerView.Adapter<chatAdapter.MessageViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        return  MessageViewHolder( LayoutInflater.from(parent.context)
            .inflate(R.layout.row_message_layout, parent, false))
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
     return  array.size
    }


    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var left_message: TextView = itemView.findViewById(R.id.leftmessage)
        var right_message: TextView = itemView.findViewById(R.id.rightmessage)
        var left_layout: ConstraintLayout = itemView.findViewById(R.id.leftcl)
        var right_layout: ConstraintLayout = itemView.findViewById(R.id.rightcl)

        fun bind(position: Int) {
            left_message.text = array[position].msg
            right_message.text = array[position].msg
            if(array[position].sender == "KELIS98")
            {
                right_layout.visibility = View.VISIBLE
                left_layout.visibility = View.INVISIBLE
            }else{
                left_layout.visibility = View.VISIBLE
                right_layout.visibility = View.INVISIBLE
            }

        }

    }
}