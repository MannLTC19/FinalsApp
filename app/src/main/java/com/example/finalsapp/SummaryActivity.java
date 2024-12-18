package com.example.finalsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SummaryActivity extends AppCompatActivity {

    TextView tvEnrollmentSummary;
    Button btnBack;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary); // Use your XML file here

        // Initialize UI components
        tvEnrollmentSummary = findViewById(R.id.tvEnrollmentSummary);
        btnBack = findViewById(R.id.btnBack);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch data from Firestore and display it in the TextView
        db.collection("enrollments")
                .document("student_enrollment")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get selected subjects and total credits
                        List<String> selectedSubjects = (List<String>) documentSnapshot.get("selectedSubjects");
                        int totalCredits = documentSnapshot.getLong("totalCredits").intValue();

                        // Create a formatted summary
                        StringBuilder summary = new StringBuilder();
                        summary.append("Selected Subjects:\n");
                        for (String subject : selectedSubjects) {
                            summary.append("- ").append(subject).append("\n");
                        }
                        summary.append("\nTotal Credits: ").append(totalCredits);

                        // Display the summary
                        tvEnrollmentSummary.setText(summary.toString());
                    } else {
                        tvEnrollmentSummary.setText("No enrollment data found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SummaryActivity.this, "Error loading data.", Toast.LENGTH_SHORT).show();
                });

        // Handle the Back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MenuActivity
                Intent intent = new Intent(SummaryActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

