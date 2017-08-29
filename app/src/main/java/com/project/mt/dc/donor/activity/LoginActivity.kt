package com.project.mt.dc.donor.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.project.mt.dc.R
import com.project.mt.dc.charity.activity.LoginActivity


class LoginActivity : android.app.Activity() {

    val mCallbackManger = com.facebook.CallbackManager.Factory.create()
    val mFirebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance()
    var progressDialog:ProgressDialog?=null

    fun onClickCharityLogin(v: android.view.View){
        val i= android.content.Intent(this, LoginActivity::class.java)
        startActivity(i)
    }


    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.project.mt.dc.R.layout.activity_donor_login)

        progressDialog= ProgressDialog(this)
        progressDialog!!.setMessage("Logging In")
        val loginButton = findViewById(com.project.mt.dc.R.id.login_button) as LoginButton
        val loginCustomButton=findViewById(R.id.login_custom_button)as LinearLayout

        val charityLoginButton=findViewById(R.id.txt_charitylogin)as LinearLayout
        charityLoginButton.setOnClickListener({
            val i=Intent(this,LoginActivity::class.java)
            startActivity(i)
        })

        loginCustomButton.setOnClickListener({
            loginButton.performClick()
            progressDialog!!.show()
        })
        loginButton.setReadPermissions("email", "public_profile")

        loginButton.registerCallback(mCallbackManger, object : com.facebook.FacebookCallback<LoginResult> {

            override fun onSuccess(result: com.facebook.login.LoginResult?) {
                handleFacebookAccessToken(result!!.accessToken)
            }

            override fun onCancel() {
                progressDialog!!.dismiss()
                android.util.Log.d("<<Cancel>>", "Donor Login cancel")
            }

            override fun onError(error: com.facebook.FacebookException?) {
                progressDialog!!.dismiss()
                android.util.Log.d("<<Error>>", "Donor Login Error")
            }
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManger.onActivityResult(requestCode, resultCode, data)
    }

    fun handleFacebookAccessToken(token: com.facebook.AccessToken) {


        val credential = com.google.firebase.auth.FacebookAuthProvider.getCredential(token.getToken())
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->

            if (task.isSuccessful) {
                checkForNextActivity()

            }

        }
    }

    fun checkForNextActivity(){
        val user = mFirebaseAuth.currentUser
        var currentUserReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("donor")

        currentUserReference.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onCancelled(p0: com.google.firebase.database.DatabaseError?) {

            }

            override fun onDataChange(dataSnapshot: com.google.firebase.database.DataSnapshot?) {
                if (dataSnapshot!!.child(user!!.uid).exists() && dataSnapshot.child(user.uid).child("donor_phone").exists()){
                    val shared = getPreferences(Context.MODE_PRIVATE)
                    var instanceId:String?= null

                    if(shared.getString("instance_id","") != null)
                    {

                        instanceId=PreferenceManager.getDefaultSharedPreferences(applicationContext).getString("instance_id", "default String")
                        FirebaseDatabase.getInstance().getReference("donor")
                                .child(user.uid)
                                .child("instance_id")
                                .setValue(instanceId)
                    }


                    progressDialog!!.dismiss()
                    val i = android.content.Intent(this@LoginActivity, DonorNavigationDrawerActivity::class.java)
                    startActivity(i)
                    finish()
                }
                else{
                    var donorBundle = android.os.Bundle()
                    if (user != null) {
                        donorBundle.putString("donorId", user.uid)
                    }
                    donorBundle.putString("donorName", user!!.displayName)
                    donorBundle.putString("donorEmail", user!!.email)
                    donorBundle.putString("donorImage", user!!.photoUrl.toString())

                    progressDialog!!.dismiss()
                    val i = android.content.Intent(this@LoginActivity, ProfileSetupActivity::class.java)
                    i.putExtra("donorbundle", donorBundle)
                    startActivity(i)
                }
            }

        })

    }
}


