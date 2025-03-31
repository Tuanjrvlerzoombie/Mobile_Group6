package com.example.due.createnote;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {

    private TextView backButton, saveButton, reminderText, lastEdited;
    private EditText noteTitle, noteContent;
    private Button importantButton, topPriorityButton, thisWeekButton;
    private ImageButton searchButton, saveBottomButton, moreButton;
    private View color1, color2, color3, color4, color5;
    private AppDatabase db;
    private Note note;
    private int backgroundColor = android.R.color.white;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Khởi tạo cơ sở dữ liệu
        db = AppDatabase.getDatabase(this);

        // Ánh xạ các thành phần giao diện
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);
        reminderText = findViewById(R.id.reminderText);
        lastEdited = findViewById(R.id.lastEdited);
        noteTitle = findViewById(R.id.noteTitle);
        noteContent = findViewById(R.id.noteContent);
        importantButton = findViewById(R.id.importantButton);
        topPriorityButton = findViewById(R.id.topPriorityButton);
        thisWeekButton = findViewById(R.id.thisWeekButton);
        searchButton = findViewById(R.id.searchButton);
        saveBottomButton = findViewById(R.id.saveBottomButton);
        moreButton = findViewById(R.id.moreButton);

        // Tải ghi chú nếu có (giả sử chỉ có 1 ghi chú để đơn giản)
        new LoadNoteTask().execute();

        // Cập nhật thời gian chỉnh sửa cuối cùng
        updateLastEditedTime();

        // Xử lý nút Back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý nút Save (trên cùng)
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        // Xử lý nút Save (dưới cùng)
        saveBottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        // Xử lý nút Search
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NoteActivity.this, "Search functionality not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút More (ba chấm)
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetMenu();
            }
        });

        // Xử lý nút Reminder
        reminderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReminder();
                Toast.makeText(NoteActivity.this, "Reminder set!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý các nút ưu tiên
        importantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPriorityButtons();
                importantButton.setBackgroundResource(R.drawable.button_background_selected);
                if (note != null) {
                    note.setPriority("Important");
                }
            }
        });

        topPriorityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPriorityButtons();
                topPriorityButton.setBackgroundResource(R.drawable.button_background_selected);
                if (note != null) {
                    note.setPriority("Top Priority");
                }
            }
        });

        thisWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPriorityButtons();
                thisWeekButton.setBackgroundResource(R.drawable.button_background_selected);
                if (note != null) {
                    note.setPriority("Should be Done This Week");
                }
            }
        });
    }

    // Lưu ghi chú
    private void saveNote() {
        String title = noteTitle.getText().toString();
        String content = noteContent.getText().toString();
        if (!title.isEmpty() && !content.isEmpty()) {
            if (note == null) {
                note = new Note();
            }
            note.setTitle(title);
            note.setContent(content);
            note.setBackgroundColor(backgroundColor);
            note.setLastEdited(System.currentTimeMillis());
            new SaveNoteTask().execute(note);
            Toast.makeText(NoteActivity.this, "Note saved!", Toast.LENGTH_SHORT).show();
            updateLastEditedTime();
        } else {
            Toast.makeText(NoteActivity.this, "Please enter title and content", Toast.LENGTH_SHORT).show();
        }
    }

    // Hiển thị menu BottomSheet
    private void showBottomSheetMenu() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_menu);

        // Ánh xạ các thành phần trong BottomSheet
        color1 = bottomSheetDialog.findViewById(R.id.color1);
        color2 = bottomSheetDialog.findViewById(R.id.color2);
        color3 = bottomSheetDialog.findViewById(R.id.color3);
        color4 = bottomSheetDialog.findViewById(R.id.color4);
        color5 = bottomSheetDialog.findViewById(R.id.color5);

        LinearLayout reminderOption = bottomSheetDialog.findViewById(R.id.reminderOption);
        LinearLayout noteTypeOption = bottomSheetDialog.findViewById(R.id.noteTypeOption);
        LinearLayout labelOption = bottomSheetDialog.findViewById(R.id.labelOption);
        LinearLayout attachmentOption = bottomSheetDialog.findViewById(R.id.attachmentOption);
        Button deleteButton = bottomSheetDialog.findViewById(R.id.deleteButton);
        Button doneButton = bottomSheetDialog.findViewById(R.id.doneButton);

        // Xử lý thay đổi màu nền
        color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundColor = android.R.color.white;
                findViewById(R.id.activity_note).setBackgroundColor(getResources().getColor(backgroundColor));
                if (note != null) {
                    note.setBackgroundColor(backgroundColor);
                }
                bottomSheetDialog.dismiss();
            }
        });

        color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundColor = android.R.color.holo_red_light;
                findViewById(R.id.activity_note).setBackgroundColor(getResources().getColor(backgroundColor));
                if (note != null) {
                    note.setBackgroundColor(backgroundColor);
                }
                bottomSheetDialog.dismiss();
            }
        });

        color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundColor = android.R.color.holo_purple;
                findViewById(R.id.activity_note).setBackgroundColor(getResources().getColor(backgroundColor));
                if (note != null) {
                    note.setBackgroundColor(backgroundColor);
                }
                bottomSheetDialog.dismiss();
            }
        });

        color4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundColor = android.R.color.holo_green_light;
                findViewById(R.id.activity_note).setBackgroundColor(getResources().getColor(backgroundColor));
                if (note != null) {
                    note.setBackgroundColor(backgroundColor);
                }
                bottomSheetDialog.dismiss();
            }
        });

        color5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundColor = android.R.color.holo_orange_light;
                findViewById(R.id.activity_note).setBackgroundColor(getResources().getColor(backgroundColor));
                if (note != null) {
                    note.setBackgroundColor(backgroundColor);
                }
                bottomSheetDialog.dismiss();
            }
        });

        // Xử lý các tùy chọn khác
        reminderOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReminder();
                Toast.makeText(NoteActivity.this, "Reminder set from menu!", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        noteTypeOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NoteActivity.this, "Change note type not implemented yet", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        labelOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NoteActivity.this, "Add label not implemented yet", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        attachmentOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NoteActivity.this, "Add attachment not implemented yet", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (note != null) {
                    new DeleteNoteTask().execute(note);
                    note = null;
                    noteTitle.setText("");
                    noteContent.setText("");
                    reminderText.setText("Reminder set 15/07/2021, 18:30");
                    resetPriorityButtons();
                    backgroundColor = android.R.color.white;
                    findViewById(R.id.activity_note).setBackgroundColor(getResources().getColor(backgroundColor));
                    Toast.makeText(NoteActivity.this, "Note deleted!", Toast.LENGTH_SHORT).show();
                }
                bottomSheetDialog.dismiss();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
                finish();
            }
        });

        bottomSheetDialog.show();
    }

    // Cập nhật thời gian chỉnh sửa cuối cùng
    private void updateLastEditedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        lastEdited.setText("Last edited on " + currentTime);
    }

    // Đặt lại màu của các nút ưu tiên
    private void resetPriorityButtons() {
        importantButton.setBackgroundResource(R.drawable.button_background);
        topPriorityButton.setBackgroundResource(R.drawable.button_background);
        thisWeekButton.setBackgroundResource(R.drawable.button_background);
    }

    // Đặt nhắc nhở
    private void setReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Đặt nhắc nhở sau 5 giây
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 5);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        reminderText.setText("Reminder set " + new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault()).format(calendar.getTime()));
    }

    // AsyncTask để tải ghi chú
    private class LoadNoteTask extends AsyncTask<Void, Void, Note> {
        @Override
        protected Note doInBackground(Void... voids) {
            return db.noteDao().getFirstNote();
        }

        @Override
        protected void onPostExecute(Note loadedNote) {
            if (loadedNote != null) {
                note = loadedNote;
                noteTitle.setText(note.getTitle());
                noteContent.setText(note.getContent());
                backgroundColor = note.getBackgroundColor();
                findViewById(R.id.activity_note).setBackgroundColor(getResources().getColor(backgroundColor));
                if (note.getPriority() != null) {
                    switch (note.getPriority()) {
                        case "Important":
                            importantButton.setBackgroundResource(R.drawable.button_background_selected);
                            break;
                        case "Top Priority":
                            topPriorityButton.setBackgroundResource(R.drawable.button_background_selected);
                            break;
                        case "Should be Done This Week":
                            thisWeekButton.setBackgroundResource(R.drawable.button_background_selected);
                            break;
                    }
                }
                lastEdited.setText("Last edited on " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(note.getLastEdited())));
            }
        }
    }

    // AsyncTask để lưu ghi chú
    private class SaveNoteTask extends AsyncTask<Note, Void, Void> {
        @Override
        protected Void doInBackground(Note... notes) {
            Note noteToSave = notes[0];
            if (noteToSave.getId() == 0) {
                db.noteDao().insert(noteToSave);
            } else {
                db.noteDao().update(noteToSave);
            }
            return null;
        }
    }

    // AsyncTask để xóa ghi chú
    private class DeleteNoteTask extends AsyncTask<Note, Void, Void> {
        @Override
        protected Void doInBackground(Note... notes) {
            db.noteDao().delete(notes[0]);
            return null;
        }
    }
}