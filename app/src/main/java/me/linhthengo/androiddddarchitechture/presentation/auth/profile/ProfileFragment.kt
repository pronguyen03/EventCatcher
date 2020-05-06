package me.linhthengo.androiddddarchitechture.presentation.auth.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.*
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream

class ProfileFragment: BaseFragment(), EasyPermissions.PermissionCallbacks {
    override fun layoutId(): Int = R.layout.fragment_profile

//    private val CAMERA_REQUEST = 1000
//    private val PERMISSION_PICK_IMAGE = 1001
//
//    lateinit var mFullname : EditText
//    lateinit var mAddress : EditText
//    lateinit var mPhone : EditText
//    lateinit var mGmail : EditText
//    lateinit var mUserImage : ImageView
//    lateinit var mUpdate : Button
//    lateinit var mDatabase : DatabaseReference
//    lateinit var mAuth : FirebaseAuth
//    lateinit var mProgressbar : ProgressDialog
//    private val GALLERY_PICK = 1
//    lateinit var StorageReference : StorageReference
//    internal var filePath: Uri? = null
//
//    lateinit var dialog: AlertDialog
//    lateinit var storage: FirebaseStorage

    // private val viewProfileStateObserver = Observer<AuthViewModel.State> { handleAuthState(it) }

    private val DEFAULT_IMAGE_URL = "https://picsum.photos/200"

    private lateinit var imageUri: Uri
    private val REQUEST_IMAGE_CAPTURE = 100

    private val currentUser = FirebaseAuth.getInstance().currentUser



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        (activity as AppCompatActivity).supportActionBar?.hide()

//        mAuth = FirebaseAuth.getInstance()
//        val uid = mAuth.currentUser?.uid
//        mDatabase = FirebaseDatabase.getInstance().getReference("User").child(uid.toString())
//        StorageReference = FirebaseStorage.getInstance().getReference()
//
//        mDatabase.addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onDataChange(data: DataSnapshot) {
//                val Fullname =data?.child("FullName")?.value!!.toString().trim()
//                val Address =data?.child("Address")?.value!!.toString().trim()
//                val Phone =data?.child("Phone")?.value!!.toString().trim()
//                val Gmail =data?.child("Gmail")?.value!!.toString().trim()
//                val image = data?.child("image")?.value!!.toString().trim()
//
//                mFullname.setText(Fullname)
//                mAddress.setText(Address)
//                mPhone.setText(Phone)
//                mGmail.setText(Gmail)
//
//                Picasso.get().load(image).into(mUserImage)
//            }
//
//        })


//        mUpdate.setOnClickListener{
//            val Fullname = mFullname.text.toString().trim()
//            val Address = mAddress.text.toString().trim()
//            val Phone = mPhone.text.toString().trim()
//            val Gmail = mGmail.text.toString().trim()
//            if(TextUtils.isEmpty(Fullname)){
//                mFullname.error = "Enter FullName"
//                return@setOnClickListener
//            }
//            if(TextUtils.isEmpty(Address)){
//                mAddress.error = "Enter Address"
//                return@setOnClickListener
//            }
//            if(TextUtils.isEmpty(Phone)){
//                mPhone.error = "Enter Phone"
//                return@setOnClickListener
//            }
//            if(TextUtils.isEmpty(Gmail)){
//                mGmail.error = "Enter Gmail"
//                return@setOnClickListener
//            }
//            updateUser(Fullname, Address, Phone, Gmail)
//        }

//        storage = FirebaseStorage.getInstance();
//        StorageReference = storage.reference
//
//        dialog = SpotsDialog.Builder().setCancelable(false).setContext(this.context).build()

//        userProfileBtnCamera.setOnClickListener(){
//            openCamera();
//        }
//        userProfileBtnChoose.setOnClickListener(){
//            ChosseImage();
//        }
//        userProfileBtn.setOnClickListener(){
//            UploadImage();
//        }
    }

//    private fun updateUser(Fullname : String, Address : String, Phone : String, Gmail : String){
//        mProgressbar.setMessage("Update wait")
//        mProgressbar.show()
//
//        val userMap = HashMap<String, Any>()
//
//        userMap["FullName"] = Fullname
//        userMap["Address"] = Address
//        userMap["Phone"] = Phone
//        userMap["Gmail"] = Gmail
//
//        mDatabase.setValue(userMap).addOnCompleteListener{ task->
//
//            if(task.isSuccessful){
////                val Intent = Intent(applicationContext , MainActivity::class.java)
////                startActivity(Intent)
//            }
//        }
//    }

//    private fun UploadImage() {
//        if(filePath != null){
//            dialog.show()
//            val reference = StorageReference.child("images/" + UUID.randomUUID().toString())
//            reference.putFile(filePath!!).addOnSuccessListener { taskSnapshot ->
//                dialog.dismiss()
//                Toast.makeText(this.context, "Upload", Toast.LENGTH_SHORT).show()
//            }.addOnFailureListener { e ->
//                dialog.dismiss()
//                Toast.makeText(this.context, "Fail", Toast.LENGTH_SHORT).show()
//            }.addOnProgressListener { taskSnapshot ->
//                val process = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
//                dialog.setMessage("Upload $process %")
//            }
//        }
//    }
//
//    private fun ChosseImage(){
//        Dexter.withActivity(this.activity).withPermissions(
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object :
//            MultiplePermissionsListener {
//            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
//                if(report!!.areAllPermissionsGranted()){
//
//                    val intent = Intent(Intent.ACTION_GET_CONTENT)
//                    intent.type = "image/*"
//                    startActivityForResult(Intent.createChooser(intent, "Select Image"),PERMISSION_PICK_IMAGE)
//                }
//                else{
////                    Toast.makeText(this@ProfileFragment, "Permission Denied!!", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onPermissionRationaleShouldBeShown(
//                permissions: MutableList<PermissionRequest>?,
//                token: PermissionToken?
//            ) {
//                token!!.continuePermissionRequest()
//            }
//        }).check()
//    }
//
//    private fun openCamera(){
//        Dexter.withActivity(this.activity).withPermissions(
//            Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object :
//            MultiplePermissionsListener {
//            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
//                if(report!!.areAllPermissionsGranted()){
//                    val values = ContentValues()
//                    values.put(MediaStore.Images.Media.TITLE, "new Picture")
//                    values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
////                    filePath = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//
//                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath)
//                    startActivityForResult(cameraIntent, CAMERA_REQUEST)
//                }
//                else{
////                    Toast.makeText(this@UserProfileActivity, "Permission Denied!!", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onPermissionRationaleShouldBeShown(
//                permissions: MutableList<PermissionRequest>?,
//                token: PermissionToken?
//            ) {
//                token!!.continuePermissionRequest()
//            }
//        }).check()
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK){
//            if(requestCode == PERMISSION_PICK_IMAGE){
//                if (data != null) {
//                    if(data.data != null){
//                        filePath = data.data
//                        try{
////                            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
////                            userImageView.setImageBitmap(bitmap)
//                        }
//                        catch (e: IOException){
//                            e.printStackTrace()
//                        }
//                    }
//                }
//            }
//            if(requestCode == CAMERA_REQUEST){
//                try{
////                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
////                    userImageView.setImageBitmap(bitmap)
//                }
//                catch (e: IOException){
//                    e.printStackTrace()
//                }
//            }
//        }
//    }


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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_back.setOnClickListener {
           findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }
        image_view.setOnClickListener {
            takePictureIntent()
        }
        currentUser?.let { user ->
            Glide.with(this)
                .load(user.photoUrl)
                .into(image_view)
            edit_text_name.setText(user.displayName)
            text_email.text = user.email

            text_phone.text = if (user.phoneNumber.isNullOrEmpty()) "Add Number" else user.phoneNumber

            if (user.isEmailVerified) {
                text_not_verified.visibility = View.INVISIBLE
            } else {
                text_not_verified.visibility = View.VISIBLE
            }
        }

        image_view.setOnClickListener {
            takePictureIntent()
        }

        text_phone.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_Phone)
        }

        text_password.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_passwordChange)
        }

        text_email.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_emailChange)
        }

        text_not_verified.setOnClickListener {

            currentUser?.sendEmailVerification()
                ?.addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
                    }
                }

        }

        button_save.setOnClickListener {

            val photo = when {
                ::imageUri.isInitialized -> imageUri
                currentUser?.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)
                else -> currentUser.photoUrl
            }

            val name = edit_text_name.text.toString().trim()

            if (name.isEmpty()) {
                edit_text_name.error = "name required"
                edit_text_name.requestFocus()
                return@setOnClickListener
            }

            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photo)
                .build()

            progressbar.visibility = View.VISIBLE

            currentUser?.updateProfile(updates)
                ?.addOnCompleteListener { task ->
                    progressbar.visibility = View.INVISIBLE
                    if (task.isSuccessful) {
                        Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
                    }
                }

        }


    }
    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadImageAndSaveUri(imageBitmap)
        }
    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        val upload = storageRef.putBytes(image)

        progressbar_pic.visibility = View.VISIBLE
        upload.addOnCompleteListener { uploadTask ->
            progressbar_pic.visibility = View.INVISIBLE

            if (uploadTask.isSuccessful) {
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it
                        Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show()
//                        activity?.toast(imageUri.toString())
                        Toast.makeText(context, imageUri.toString(), Toast.LENGTH_LONG)
                        image_view.setImageBitmap(bitmap)
                    }
                }
            } else {
                uploadTask.exception?.let {
                  Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

}