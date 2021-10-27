package com.example.firebasestatusapp

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Need to move this to its own class
data class User(
    val displayName: String = "",
    val emojis: String = ""
)

class UserViewHolder( itemView: View): RecyclerView.ViewHolder( itemView )

class MainActivity : AppCompatActivity() {
    companion object{
        private const val TAG = "MainActivity"
    }
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth

        // TODO: Move this to ViewModel at least
        val query = db.collection("users")
        val options = FirestoreRecyclerOptions.Builder<User>().setQuery(
            query,
            User::class.java
        ).setLifecycleOwner( this ).build()

        // TODO: Create its own adapter class
        val adapter = object: FirestoreRecyclerAdapter<User, UserViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                val user = LayoutInflater.from( this@MainActivity)
                    .inflate(android.R.layout.simple_list_item_2,
                    parent, false)

                return UserViewHolder( user )
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
                val tvName: TextView = holder.itemView.findViewById( android.R.id.text1 )
                val tvEmojis: TextView = holder.itemView.findViewById( android.R.id.text2 )

                tvName.text = model.displayName
                tvEmojis.text = model.emojis
            }

        }
        val rvUsers: RecyclerView = findViewById(R.id.rvUsers)
        rvUsers.adapter = adapter
        rvUsers.layoutManager = LinearLayoutManager(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if( item.itemId == R.id.miLogout ){
            Log.i( TAG, "LogOut user" )

            auth.signOut()

            val logoutIntent = Intent( this, LoginActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity( logoutIntent )
        } else if( item.itemId == R.id.miEdit ){
            Log.i( TAG, "Editing status" )

            showAlertDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    inner class EmojiFilter() : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence {
            // if valid, return source
            // else return ""
            if( source == null || source.isBlank())
                return ""

            Log.i( TAG, "Added text $source has a length of ${source.length}")

            val validTypes = listOf(
                Character.NON_SPACING_MARK, // 6
                Character.DECIMAL_DIGIT_NUMBER, // 9
                Character.LETTER_NUMBER, // 10
                Character.OTHER_NUMBER, // 11
                Character.SPACE_SEPARATOR, // 12
                Character.FORMAT, // 16
                Character.SURROGATE, // 19
                Character.DASH_PUNCTUATION, // 20
                Character.START_PUNCTUATION, // 21
                Character.END_PUNCTUATION, // 22
                Character.CONNECTOR_PUNCTUATION, // 23
                Character.OTHER_PUNCTUATION, // 24
                Character.MATH_SYMBOL, // 25
                Character.CURRENCY_SYMBOL, //26
                Character.MODIFIER_SYMBOL, // 27
                Character.OTHER_SYMBOL // 28
            ).map{
                it.toInt()
            }

            for( inputChar in source ){
                val type = Character.getType( inputChar )

                Log.i( TAG, "Character Type is $type")
                if( !validTypes.contains( type ) ){
                    Toast.makeText( this@MainActivity, "Only Emojis are allowed", Toast.LENGTH_LONG).show()
                    return ""
                }
            }

            return source
        }

    }
    private fun showAlertDialog() {
        // TODO: Define an XML file if we want more control
        val editText = EditText( this )
        val inputFilter = InputFilter.LengthFilter( 9 )
        val emojiFilter = EmojiFilter()

        editText.filters = arrayOf( inputFilter, emojiFilter )
        val dialog = AlertDialog.Builder( this )
            .setTitle( "Update Your Emoji")
            .setView(editText)
            .setNegativeButton("Cancel", null )
            .setPositiveButton("OK", null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            Log.i( TAG, "Positive Button Clicked" )
            val emojisEntered = editText.text.toString()

            if( emojisEntered.isBlank() ){
                Toast.makeText(this, "Cannot Submit Empty Text", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val currentUser = auth.currentUser
            if( currentUser == null ){
                Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG ).show()
                return@setOnClickListener
            }

            db.collection("users").document(currentUser.uid)
                .update( "emojis", emojisEntered )

            dialog.dismiss()
        }
    }
}