package itstep.learning.android_213;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView tv1;
    private TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        //    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        //    return insets;
        //});
        tv1 =findViewById( R.id.tex_view1);
        tv2 =findViewById( R.id.tex_view2);
        findViewById( R.id.button1).setOnClickListener(this::onButton1Click);
        findViewById( R.id.button_plus).setOnClickListener(this::onButtonPlus);
        findViewById( R.id.button_minus).setOnClickListener(this::onButtonMinus);
    }

    private void onButton1Click(View view){
        String txt = tv1.getText().toString();
        txt += "!";
        tv1.setText(txt);
    }
    private void onButtonPlus(View view){
        String txt = tv2.getText().toString();
        int value = txt.isEmpty() ? 0 : Integer.parseInt(txt);
        value += 1;
        tv2.setText(String.valueOf(value));
    }
    private void onButtonMinus(View view){
        String txt = tv2.getText().toString();
        int value = txt.isEmpty() ? 0 : Integer.parseInt(txt);
        value -= 1;
        tv2.setText(String.valueOf(value));
    }
}