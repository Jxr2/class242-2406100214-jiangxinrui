package cn.edu.btbu.myfirstapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView tvDetail = findViewById(R.id.tvDetail);
        Button btnBack = findViewById(R.id.btnBack);

        Intent intent = getIntent();
        String amount = intent.getStringExtra("amount");
        String type = intent.getStringExtra("type");
        String category = intent.getStringExtra("category");
        boolean fixed = intent.getBooleanExtra("fixed", false);
        String note = intent.getStringExtra("note");

        if (note == null || note.isEmpty()) {
            note = "无";
        }

        String text = "金额：¥" + amount
                + "\n类型：" + type
                + "\n分类：" + category
                + "\n是否固定：" + (fixed ? "固定" : "非固定")
                + "\n备注：" + note;

        tvDetail.setText(text);
        btnBack.setOnClickListener(v -> finish());
    }
}