import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ImagePicDialog(private val activity: Activity) {

    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = firebaseStorage.reference
    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun chooseImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY)
    }

    fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val userId = firebaseAuth.currentUser?.uid
        userId?.let { uid ->
            val imageName = "profile_image.jpg" // nazwa pliku w Storage
            val imageRef = storageReference.child("images/$imageName")

            imageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageURL = uri.toString()

                        // Zapisz link do obrazu w bazie danych
                        val userRef = databaseReference.child("userInfo").child(uid)
                        userRef.child("profileImageURL").setValue(imageURL)
                            .addOnSuccessListener {
                                Toast.makeText(activity, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(activity, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    companion object {
        const val REQUEST_CODE_GALLERY = 123
    }
}