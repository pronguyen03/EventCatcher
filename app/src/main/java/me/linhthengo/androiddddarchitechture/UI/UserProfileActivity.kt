package me.linhthengo.androiddddarchitechture.UI

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_user_profile.*
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.presentation.MainActivity
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


class UserProfileActivity : AppCompatActivity() ,EasyPermissions.PermissionCallbacks {

    private val CAMERA_REQUEST = 1000
    private val PERMISSION_PICK_IMAGE = 1001

    lateinit var mFullname : EditText
    lateinit var mAddress : EditText
    lateinit var mPhone : EditText
    lateinit var mGmail : EditText
    lateinit var mUserImage : ImageView
    lateinit var mUpdate : Button
    lateinit var mDatabase : DatabaseReference
    lateinit var mAuth : FirebaseAuth
    lateinit var mProgressbar : ProgressDialog
    private val GALLERY_PICK = 1
    lateinit var StorageReference : StorageReference
    internal var filePath: Uri? = null

    lateinit var dialog: AlertDialog
    lateinit var storage: FirebaseStorage


    private val LOCATION_AND_CONTACTS = arrayOf<String>(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE ,android.Manifest.permission.CAMERA)
    private val RC_CAMERA_PERM = 123
    private val RC_LOCATION_AND_CONTACTS_PERM = 124

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        mFullname = findViewById(R.id.userFullName)
        mAddress = findViewById(R.id.userAddress)
        mUserImage = findViewById(R.id.userImageView)
        mPhone = findViewById(R.id.userPhone)
        mGmail = findViewById(R.id.userGmail)
        mUpdate = findViewById(R.id.userProfileBtn)
        mProgressbar = ProgressDialog(this)

        mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid
        mDatabase = FirebaseDatabase.getInstance().getReference("User").child(uid.toString())
        StorageReference = FirebaseStorage.getInstance().getReference()

        mDatabase.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(data: DataSnapshot) {
                val Fullname =data?.child("FullName")?.value!!.toString().trim()
                val Address =data?.child("Address")?.value!!.toString().trim()
                val Phone =data?.child("Phone")?.value!!.toString().trim()
                val Gmail =data?.child("Gmail")?.value!!.toString().trim()
                val image = data?.child("image")?.value!!.toString().trim()

                mFullname.setText(Fullname)
                mAddress.setText(Address)
                mPhone.setText(Phone)
                mGmail.setText(Gmail)

                Picasso.get().load(image).into(mUserImage)
            }

        })


        mUpdate.setOnClickListener{
            val Fullname = mFullname.text.toString().trim()
            val Address = mAddress.text.toString().trim()
            val Phone = mPhone.text.toString().trim()
            val Gmail = mGmail.text.toString().trim()
            if(TextUtils.isEmpty(Fullname)){
                mFullname.error = "Enter FullName"
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(Address)){
                mAddress.error = "Enter Address"
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(Phone)){
                mPhone.error = "Enter Phone"
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(Gmail)){
                mGmail.error = "Enter Gmail"
                return@setOnClickListener
            }
            updateUser(Fullname, Address, Phone, Gmail)
        }

        storage = FirebaseStorage.getInstance();
        StorageReference = storage.reference

        dialog = SpotsDialog.Builder().setCancelable(false).setContext(this).build()

        userProfileBtnCamera.setOnClickListener(){
            openCamera();
        }
        userProfileBtnChoose.setOnClickListener(){
            ChosseImage();
        }
        userProfileBtn.setOnClickListener(){
            UploadImage();
        }
    }

    private fun UploadImage() {
        if(filePath != null){
            dialog.show()
            val reference = StorageReference.child("images/" +UUID.randomUUID().toString())
            reference.putFile(filePath!!).addOnSuccessListener { taskSnapshot ->
                dialog.dismiss()
                Toast.makeText(this@UserProfileActivity, "Upload", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                dialog.dismiss()
                Toast.makeText(this@UserProfileActivity, "Fail", Toast.LENGTH_SHORT).show()
            }.addOnProgressListener { taskSnapshot ->
                val process = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                dialog.setMessage("Upload $process %")
            }
        }
    }

    private fun ChosseImage(){
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object : MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){

                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    startActivityForResult(Intent.createChooser(intent, "Select Image"),PERMISSION_PICK_IMAGE)
                }
                else{
                    Toast.makeText(this@UserProfileActivity, "Permission Denied!!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                token!!.continuePermissionRequest()
            }
        }).check()
    }

    private fun openCamera(){
        Dexter.withActivity(this).withPermissions(Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object : MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.TITLE, "new Picture")
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                    filePath = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath)
                    startActivityForResult(cameraIntent, CAMERA_REQUEST)
                }
                else{
                    Toast.makeText(this@UserProfileActivity, "Permission Denied!!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                token!!.continuePermissionRequest()
            }
        }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if(requestCode == PERMISSION_PICK_IMAGE){
                if (data != null) {
                    if(data.data != null){
                        filePath = data.data
                        try{
                            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                            userImageView.setImageBitmap(bitmap)
                        }
                        catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                }
            }
            if(requestCode == CAMERA_REQUEST){
                try{
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    userImageView.setImageBitmap(bitmap)
                }
                catch (e: IOException){
                    e.printStackTrace()
                }
            }
        }
    }


    private fun updateUser(Fullname : String, Address : String, Phone : String, Gmail : String){
        mProgressbar.setMessage("Update wait")
        mProgressbar.show()

        val userMap = HashMap<String, Any>()

        userMap["FullName"] = Fullname
        userMap["Address"] = Address
        userMap["Phone"] = Phone
        userMap["Gmail"] = Gmail

        mDatabase.setValue(userMap).addOnCompleteListener{ task->

            if(task.isSuccessful){
                val Intent = Intent(applicationContext , MainActivity::class.java)
                startActivity(Intent)
                finish()
            }
        }
    }




    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    private fun hasLocationAndContactsPermissions(): Boolean{
        return EasyPermissions.hasPermissions(this, *LOCATION_AND_CONTACTS)
    }
}
