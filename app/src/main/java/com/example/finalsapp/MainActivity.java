package com.example.finalsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView lvSubjects;
    Button btnShowSummary, btnDropAll;

    String[] subjects = {
            "Probability and Statistics - 3 Credits",
            "Economic Survival 2 - 3 Credits",
            "Server-Side Internet Programming - 3 Credits",
            "Linear Algebra - 3 Credits",
            "Computer Network - 3 Credits",
            "Object Oriented and Visual Programming - 3 Credits",
            "Database System - 3 Credits",
            "Software Engineering - 3 Credits",
            "Artificial Intelligence - 3 Credits"
    };

    HashMap<String, Integer> subjectCredits = new HashMap<>();
    ArrayList<String> selectedSubjects = new ArrayList<>();

    int totalCredits = 0;
    final int MAX_CREDITS = 24;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvSubjects = findViewById(R.id.lvSubjects);
        btnShowSummary = findViewById(R.id.btnShowSummary);
        btnDropAll = findViewById(R.id.btnDropAll);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        subjectCredits.put("Probability and Statistics", 3);
        subjectCredits.put("Economic Survival 2", 3);
        subjectCredits.put("Server-Side Internet Programming", 3);
        subjectCredits.put("Linear Algebra", 3);
        subjectCredits.put("Computer Network", 3);
        subjectCredits.put("Object Oriented and Visual Programming", 3);
        subjectCredits.put("Database System", 3);
        subjectCredits.put("Software Engineering", 3);
        subjectCredits.put("Artificial Intelligence", 3);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, subjects);
        lvSubjects.setAdapter(adapter);
        lvSubjects.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        lvSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String subjectLine = subjects[position];
                String subjectName = subjectLine.split(" - ")[0];
                int subjectCredit = subjectCredits.get(subjectName);

                if (lvSubjects.isItemChecked(position)) {
                    if (totalCredits + subjectCredit > MAX_CREDITS) {
                        lvSubjects.setItemChecked(position, false);
                        Toast.makeText(MainActivity.this,
                                "Cannot select " + subjectName + ". Credit limit exceeded!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        totalCredits += subjectCredit;
                    }
                } else {
                    totalCredits -= subjectCredit;
                }
            }
        });

        btnShowSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirestore();
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
        for (int i = 0; i < lvSubjects.getCount(); i++) {
            lvSubjects.setItemChecked(i, false);
        }

        totalCredits = 0;
        selectedSubjects.clear();

        db.collection("enrollments")
                .document("student_enrollment")
                .delete()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(MainActivity.this, "All selections dropped successfully!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Error dropping selections!", Toast.LENGTH_SHORT).show()
                );
    }



    private void saveDataToFirestore() {
        selectedSubjects.clear();

        for (int i = 0; i < lvSubjects.getCount(); i++) {
            if (lvSubjects.isItemChecked(i)) {
                String subjectLine = subjects[i];
                selectedSubjects.add(subjectLine);
            }
        }

        if (selectedSubjects.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please select at least one subject.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> enrollmentData = new HashMap<>();
        enrollmentData.put("selectedSubjects", selectedSubjects);
        enrollmentData.put("totalCredits", totalCredits);

        db.collection("enrollments")
                .document("student_enrollment")
                .set(enrollmentData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Enrollment saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error saving data.", Toast.LENGTH_SHORT).show();
                });
    }
}

