package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvResult, tvExpression;
    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private Button btnDot, btnAdd, btnSub, btnMul, btnDiv, btnMod, btnDel, btnEq, btnBack;

    private StringBuilder bieuThucDangNhap = new StringBuilder();
    private boolean kyTuCuoiLaToanTu = false;
    private boolean vuaTinhXong = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvResult = findViewById(R.id.tvResult);
        tvExpression = findViewById(R.id.tvExpression);

        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btnDot = findViewById(R.id.btnDot);
        btnAdd = findViewById(R.id.btnAdd);
        btnSub = findViewById(R.id.btnSub);
        btnMul = findViewById(R.id.btnMul);
        btnDiv = findViewById(R.id.btnDiv);
        btnMod = findViewById(R.id.btnMod);
        btnDel = findViewById(R.id.btnDel);
        btnEq = findViewById(R.id.btnEq);
        btnBack = findViewById(R.id.btnBack);

        Button[] buttons = {
                btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9,
                btnDot, btnAdd, btnSub, btnMul, btnDiv, btnMod, btnDel, btnEq, btnBack
        };

        for (Button btn : buttons) {
            btn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button) v;

        if (btn == btnDel) {
            bieuThucDangNhap.setLength(0);
            tvExpression.setText("");
            tvResult.setText("0");
            vuaTinhXong = false;
            kyTuCuoiLaToanTu = false;
            return;
        }

        if (btn == btnBack) {
            if (bieuThucDangNhap.length() > 0) {
                bieuThucDangNhap.deleteCharAt(bieuThucDangNhap.length() - 1);
                tvResult.setText(bieuThucDangNhap.toString());
                if (bieuThucDangNhap.length() > 0) {
                    char kyTuCuoi = bieuThucDangNhap.charAt(bieuThucDangNhap.length() - 1);
                    kyTuCuoiLaToanTu = laToanTu(kyTuCuoi);
                } else {
                    kyTuCuoiLaToanTu = false;
                }
            }
            return;
        }

        if (btn == btnEq) {
            try {
                String bieuThuc = bieuThucDangNhap.toString().replace("×", "*").replace("÷", "/");
                double ketQua = tinhKetQua(bieuThuc);
                tvExpression.setText(bieuThucDangNhap.toString());
                tvResult.setText(dinhDangKetQua(ketQua));
                bieuThucDangNhap.setLength(0);
                bieuThucDangNhap.append(ketQua);
                vuaTinhXong = true;
            } catch (Exception e) {
                tvResult.setText("Lỗi");
            }
            return;
        }

        if (btn == btnAdd || btn == btnSub || btn == btnMul || btn == btnDiv || btn == btnMod) {
            if (!kyTuCuoiLaToanTu && bieuThucDangNhap.length() > 0) {
                bieuThucDangNhap.append(kyTuToanTu(btn));
                kyTuCuoiLaToanTu = true;
                vuaTinhXong = false;
            }
        } else {
            if (vuaTinhXong) {
                bieuThucDangNhap.setLength(0);
                tvExpression.setText("");
                vuaTinhXong = false;
            }
            bieuThucDangNhap.append(btn.getText().toString());
            kyTuCuoiLaToanTu = false;
        }

        tvResult.setText(bieuThucDangNhap.toString());
    }

    private boolean laToanTu(char c) {
        return c == '+' || c == '-' || c == '×' || c == '÷' || c == '%';
    }

    private String kyTuToanTu(Button btn) {
        if (btn == btnAdd) return "+";
        if (btn == btnSub) return "-";
        if (btn == btnMul) return "×";
        if (btn == btnDiv) return "÷";
        if (btn == btnMod) return "%";
        return "";
    }

    private String dinhDangKetQua(double ketQua) {
        if (ketQua == (long) ketQua) {
            return String.format("%d", (long) ketQua);
        } else {
            String text = String.valueOf(ketQua);
            if (text.length() > 15) {
                text = text.substring(0, 15);
            }
            return text;
        }
    }

    private double tinhKetQua(String bieuThuc) {
        return new Object() {
            int viTri = -1, kyTu;

            void kyTuTiepTheo() {
                kyTu = (++viTri < bieuThuc.length()) ? bieuThuc.charAt(viTri) : -1;
            }

            boolean anKyTu(int kyTuCanAn) {
                while (kyTu == ' ') kyTuTiepTheo();
                if (kyTu == kyTuCanAn) {
                    kyTuTiepTheo();
                    return true;
                }
                return false;
            }

            double phanTich() {
                kyTuTiepTheo();
                double x = bieuThucCongTru();
                if (viTri < bieuThuc.length()) throw new RuntimeException("Ký tự lạ: " + (char) kyTu);
                return x;
            }

            double bieuThucCongTru() {
                double x = bieuThucNhanChia();
                while (true) {
                    if (anKyTu('+')) x += bieuThucNhanChia();
                    else if (anKyTu('-')) x -= bieuThucNhanChia();
                    else return x;
                }
            }

            double bieuThucNhanChia() {
                double x = nhanSo();
                while (true) {
                    if (anKyTu('*')) x *= nhanSo();
                    else if (anKyTu('/')) x /= nhanSo();
                    else if (anKyTu('%')) x %= nhanSo();
                    else return x;
                }
            }

            double nhanSo() {
                if (anKyTu('+')) return nhanSo();
                if (anKyTu('-')) return -nhanSo();

                double x;
                int viTriBatDau = this.viTri;
                if ((kyTu >= '0' && kyTu <= '9') || kyTu == '.') {
                    while ((kyTu >= '0' && kyTu <= '9') || kyTu == '.') kyTuTiepTheo();
                    x = Double.parseDouble(bieuThuc.substring(viTriBatDau, this.viTri));
                } else {
                    throw new RuntimeException("Ký tự không hợp lệ: " + (char) kyTu);
                }

                return x;
            }
        }.phanTich();
    }
}
