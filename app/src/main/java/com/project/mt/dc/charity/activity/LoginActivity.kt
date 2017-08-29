package com.project.mt.dc.charity.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.FirebaseDatabase
import com.project.mt.dc.R


class LoginActivity : Activity() {

    var txt_charityEmail: EditText?= null
    var txt_charityPassword: EditText?= null
    val firebaseAuth= FirebaseAuth.getInstance()
    var progressDialog:ProgressDialog?=null

    fun onClickCharityLogin(){

        if (txt_charityEmail!!.text.isNullOrEmpty()){
            progressDialog!!.dismiss()
            txt_charityEmail!!.error = "Please Enter Email"
            txt_charityEmail!!.requestFocus()
        }
        else if(txt_charityPassword!!.text.isNullOrEmpty()){
            progressDialog!!.dismiss()
            txt_charityPassword!!.error = "Please Enter Password"
            txt_charityPassword!!.requestFocus()
        }
        else {
            firebaseAuth.signInWithEmailAndPassword(txt_charityEmail!!.text.toString(), txt_charityPassword!!.text.toString())
                    .addOnCompleteListener({ task ->
                        if (task.isSuccessful) {

                            val shared = getPreferences(Context.MODE_PRIVATE)
                            var instanceId: String? = null


                            if (shared.getString("instance_id", "") != null) {

                                val currentUser = firebaseAuth.currentUser!!.uid
                                instanceId = PreferenceManager.getDefaultSharedPreferences(applicationContext).getString("instance_id", "default String")
                                FirebaseDatabase.getInstance().getReference("charity")
                                        .child(currentUser)
                                        .child("instance_id")
                                        .setValue(instanceId)
                            }

                            android.os.Handler().postDelayed({
                                progressDialog!!.dismiss()
                                val i = Intent(this, CharityNavigationDrawerActivity::class.java)
                                startActivity(i)
                                finish()
                            }, 1500)


                        } else {
                            progressDialog!!.dismiss()

                            try {
                                throw task.exception!!!!
                            } catch (e: FirebaseAuthWeakPasswordException) {
                                txt_charityPassword!!.error = "Please Enter Strong Password"
                                txt_charityPassword!!.requestFocus()
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                txt_charityEmail!!.error = "The Email or Password is invalid"
                                txt_charityPassword!!.error="The Email or Password is invalid"
                            } catch (e: FirebaseAuthInvalidUserException) {
                                txt_charityEmail!!.error = "There is no account with this email.Please request for account first."
                                txt_charityEmail!!.requestFocus()
                            } catch (e: Exception) {
                                Toast.makeText(this@LoginActivity, "" + task.exception,
                                        Toast.LENGTH_SHORT).show()
                            }

                        }

                    })
        }
    }
    fun onClickRequest(){

        val i= Intent(this,CharityRegisterActivity::class.java)
        startActivity(i)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charity_login)
        txt_charityEmail=findViewById(R.id.txt_charityemail) as EditText
        txt_charityPassword=findViewById(R.id.txt_charitypw) as EditText
        val btn_login=findViewById(R.id.btn_charityLogin)as LinearLayout
        val btn_request=findViewById(R.id.btn_charityrequest)as TextView

        progressDialog= ProgressDialog(this)
        progressDialog!!.setMessage("Logging In")
        btn_login.setOnClickListener({
            progressDialog!!.show()
            onClickCharityLogin()
        })
        btn_request.setOnClickListener({
            onClickRequest()
        })

    }

}
