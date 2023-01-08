package com.example.snapchat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

private val String.key: String?
    get() = Unit.toString()
private val Any.value: Any
    get() = Unit

class SnapsActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var snapsListView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var snaps: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        snapsListView = findViewById(R.id.snapsListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        snapsListView?.adapter = adapter

        // FIREBASE
        FirebaseDatabase.getInstance().getReference().child("users")
            .child(mAuth.currentUser?.uid.toString()).child("snaps")
            .addChildEventListener(object : ChildEventListener {
                // This method is triggered when a new child is added to the location to which this listener was added.
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    emails.add(snapshot.child("from").value as String)
                    snaps.add(snapshot!!.toString())
                    adapter.notifyDataSetChanged()
                }

                // This method is triggered when the data at a child location has changed.
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                // This method is triggered when a child is removed from the location to which this listener was
                override fun onChildRemoved(snapshot: DataSnapshot) {}

                // This method is triggered when a child location's priority changes. See [ ][DatabaseReference.setPriority] and [Ordered Data](https://firebase.google.com/docs/database/android/retrieve-data#data_order) for more information on priorities and ordering data.
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                // This method will be triggered in the event that this listener either failed at the server, or
                override fun onCancelled(error: DatabaseError) {
//                    var index = 0
//                    for (snap: DataSnapshot in snaps){
//                        if (snap.key == snap.key) {
//                            snaps.removeAt(index)
//                            emails.removeAt(index)
//                        }
//                        index ++
//                    }
//                    adapter.notifyDataSetChanged()
                }
            })

        snapsListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val snapshot = snaps.get(i)

            var intent = Intent(this, ViewSnapActivity::class.java)

            intent.putExtra("imageName", snapshot.child("imageName").value as String)
            intent.putExtra("imageUrl", snapshot.child("imageUrl").value as String)
            intent.putExtra("message", snapshot.child("message").value as String)
            intent.putExtra("snapKey", snapshot.key)

            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.snaps, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.createSnap) {
            val intent = Intent(this, CreateSnapActivity::class.java)
            startActivity(intent)
        } else {
            if (item.itemId == R.id.log_out) {
                mAuth.signOut()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mAuth.signOut()
    }
}

private fun String.child(s: String) {}
