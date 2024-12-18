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
    Button btnBack, btnDropAll;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        tvEnrollmentSummary = findViewById(R.id.tvEnrollmentSummary);
        btnBack = findViewById(R.id.btnBack);
        btnDropAll = findViewById(R.id.btnDropAll);

        db = FirebaseFirestore.getInstance();

        db.collection("enrollments")
                .document("student_enrollment")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> selectedSubjects = (List<String>) documentSnapshot.get("selectedSubjects");
                        int totalCredits = documentSnapshot.getLong("totalCredits").intValue();

                        StringBuilder summary = new StringBuilder();
                        summary.append("Selected Subjects:\n");
                        for (String subject : selectedSubjects) {
                            summary.append("- ").append(subject).append("\n");
                        }
                        summary.append("\nTotal Credits: ").append(totalCredits);

                        tvEnrollmentSummary.setText(summary.toString());
                    } else {
                        tvEnrollmentSummary.setText("No enrollment data found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SummaryActivity.this, "Error loading data.", Toast.LENGTH_SHORT).show();
                });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnDropAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropAllSelections();
            }
        });
    }
    private void dropAllSelections() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("enrollments")
                .document("student_enrollment")
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SummaryActivity.this, "All selections dropped successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SummaryActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(SummaryActivity.this, "Error dropping selections!", Toast.LENGTH_SHORT).show()
                );
    }
}

