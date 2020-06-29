package com.smokinmonkey.nada.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.smokinmonkey.nada.R
import com.smokinmonkey.nada.util.User
import com.smokinmonkey.nada.util.loadUrl

// custom adapter for potential match list
class PotentialMatchListAdapter(context: Context?, resourceId: Int, users: List<User>):
    ArrayAdapter<User>(context!!, resourceId, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var user = getItem(position)
        var finalView = convertView?: LayoutInflater.from(context).inflate(R.layout.match_item, parent, false)

        var name = finalView.findViewById<TextView>(R.id.tv_match_item_name)
        var image = finalView.findViewById<ImageView>(R.id.iv_match_item_image)
        var container = finalView.findViewById<ViewGroup>(R.id.conlay_match_item)

        user?.let {
            name.text = it.username
            image.loadUrl(it.imageUrl)
        }

        container.setOnClickListener {
            Toast.makeText(context, "User profile clicked, implement activity later", Toast.LENGTH_SHORT).show()
        }

        return finalView
    }
}
