package cn.edu.btbu.myfirstapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText etAmount, etNote;
    private RadioGroup rgType;
    private Spinner spCategory;
    private CheckBox cbFixed;
    private ListView lvRecords;
    private TextView tvTotal;
    private double totalAmount = 0.0;

    private ArrayAdapter<String> adapter;
    private final ArrayList<String> records = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        rgType = findViewById(R.id.rgType);
        spCategory = findViewById(R.id.spCategory);
        cbFixed = findViewById(R.id.cbFixed);
        lvRecords = findViewById(R.id.lvRecords);
        tvTotal = findViewById(R.id.tvTotal);

        String[] categories = {"餐饮", "交通", "购物", "学习", "其他"};
        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(spAdapter);

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                records
        );
        lvRecords.setAdapter(adapter);

        findViewById(R.id.btnAdd).setOnClickListener(v -> addRecord());
        findViewById(R.id.btnDetail).setOnClickListener(v -> goDetail());
        findViewById(R.id.btnClear).setOnClickListener(v -> clearForm());

        lvRecords.setOnItemLongClickListener((parent, view, position, id) -> {
            new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("删除记录")
                    .setMessage("确定要删除这条记录吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        String deleteRecord = records.get(position);
                        try {
                            String[] parts = deleteRecord.split("\\|");
                            if (parts.length >= 3) {
                                String typeStr = parts[0].trim();
                                String amountStr = parts[2].replace("¥", "").trim();
                                double delAmount = Double.parseDouble(amountStr);

                                if (typeStr.equals("收入")) {
                                    totalAmount -= delAmount;
                                } else {
                                    totalAmount += delAmount;
                                }
                                tvTotal.setText("当前结余：¥" + totalAmount);
                            }
                        } catch (Exception e) {
                        }

                        records.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        });
    }

    private String getType() {
        return rgType.getCheckedRadioButtonId() == R.id.rbIncome ? "收入" : "支出";
    }

    private void addRecord() {
        String amount = etAmount.getText().toString().trim();
        if (amount.isEmpty()) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double check = Double.parseDouble(amount);
            if (check <= 0) {
                Toast.makeText(this, "金额必须大于0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "金额格式错误", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = spCategory.getSelectedItem().toString();
        boolean fixed = cbFixed.isChecked();
        String note = etNote.getText().toString().trim();

        String fixedText = fixed ? "固定" : "非固定";
        String noteText = note.isEmpty() ? "" : " | " + note;

        String record = getType()
                + " | " + category
                + " | ¥" + amount
                + " | " + fixedText
                + noteText;

        records.add(record);

        try {
            double addAmount = Double.parseDouble(amount);
            if (getType().equals("收入")) {
                totalAmount += addAmount;
            } else {
                totalAmount -= addAmount;
            }
            tvTotal.setText("当前结余：¥" + totalAmount);
        } catch (Exception e) {}

        adapter.notifyDataSetChanged();
        Toast.makeText(this, "已添加一条记录", Toast.LENGTH_SHORT).show();
    }

    private void goDetail() {
        String amount = etAmount.getText().toString().trim();
        if (amount.isEmpty()) {
            Toast.makeText(this, "请先输入金额，再查看详情", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double check = Double.parseDouble(amount);
            if (check <= 0) {
                Toast.makeText(this, "金额必须大于0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "金额格式错误", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("amount", amount);
        intent.putExtra("type", getType());
        intent.putExtra("category", spCategory.getSelectedItem().toString());
        intent.putExtra("fixed", cbFixed.isChecked());
        intent.putExtra("note", etNote.getText().toString().trim());
        startActivity(intent);
    }

    private void clearForm() {
        etAmount.setText("");
        etNote.setText("");
        rgType.check(R.id.rbExpense);
        spCategory.setSelection(0);
        cbFixed.setChecked(false);
    }
}