package sbts.dmw.com.sbtrackingsystem.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sbts.dmw.com.sbtrackingsystem.R;
import sbts.dmw.com.sbtrackingsystem.classes.SingletonClass;


public class RegisterUserActivity extends AppCompatActivity {
    EditText full_name, dob, email, mobile1, mobile2, address, city, pincode, id, name;

    RadioGroup gender;
    Button photo;

    String User;
    Bitmap bitmap;

    final int PICK_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        photo=findViewById(R.id.register_photo);
        assert getSupportActionBar() != null;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    public void choosephoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_CODE);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CODE && resultCode == RESULT_OK && data != null) {

            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                photo.setText("Selected");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private String imagetoString(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void upload() {

        String imageURL = "https://sbts2019.000webhostapp.com/uploadprofile.php";

        StringRequest imagerequest = new StringRequest(Request.Method.POST, imageURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("name", User);
                params.put("image", imagetoString(bitmap));

                return params;
            }
        };

        SingletonClass.getInstance(getApplicationContext()).addToRequestQueue(imagerequest);
    }

}
