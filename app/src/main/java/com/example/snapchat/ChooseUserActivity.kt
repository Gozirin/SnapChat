package com.example.snapchat

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChooseUserActivity : AppCompatActivity() {
    // VARIABLES DECLARED
    var chooseUserListView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var keys: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        chooseUserListView = findViewById(R.id.chooseUserLIstView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        chooseUserListView?.adapter = adapter

        // REALTIME DATABASE
        FirebaseDatabase.getInstance()
            .getReference()
            .child("users")
            .addChildEventListener(object : ChildEventListener {
                // This method is triggered when a new child is added to the location to which this listener was added.
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val email = snapshot?.child("email")?.value as String
                    emails.add(email)
                    keys.add(snapshot.key!!)
                    adapter.notifyDataSetChanged()
                }

                // This method is triggered when the data at a child location has changed.
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                // This method is triggered when a child is removed from the location to which this listener was added.
                override fun onChildRemoved(snapshot: DataSnapshot) {}

                // This method is triggered when a child location's priority changes. See [ ][DatabaseReference.setPriority] and [Ordered Data](https://firebase.google.com/docs/database/android/retrieve-data#data_order) for more information on priorities and ordering data.
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                // This method will be triggered in the event that this listener either failed at the server, or
                // is removed as a result of the security and Firebase rules. For more information on securing your data, see: [ Security Quickstart](https://firebase.google.com/docs/database/security/quickstart)
                override fun onCancelled(error: DatabaseError) {}
            })

        chooseUserListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val snapMap: String? = mapOf( "from" to FirebaseAuth.getInstance().currentUser!!.email!!,
                    "imageName" to intent.getStringExtra("imageName"),
                    "imageUrl" to intent.getStringExtra("imageUrl"),
                    "message" to intent.getStringExtra("message")).toString()

                 // FIREBASE
            FirebaseDatabase.getInstance().getReference().child("users")
                .child(keys.get(i)).child("snaps").push().setValue(snapMap)

            // MOVE TO NEXT ACTIVITY
            val intent = Intent(this, SnapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}
