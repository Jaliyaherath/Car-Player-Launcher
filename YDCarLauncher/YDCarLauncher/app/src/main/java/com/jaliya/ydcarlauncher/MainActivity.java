package com.jaliya.ydcarlauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private final Handler handler = new Handler();
    private TextView clockView, dateView;
    private final Runnable clockTick = new Runnable() {
        @Override public void run() {
            if (clockView != null) {
                clockView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                dateView.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(new Date()));
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        buildDashboard();
    }

    private GradientDrawable bg(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        d.setStroke(1, Color.rgb(57, 66, 78));
        return d;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private Button tile(String label, Runnable action) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(16);
        b.setAllCaps(false);
        b.setBackground(bg(Color.rgb(31, 40, 52), 22));
        b.setOnClickListener(v -> action.run());
        return b;
    }

    private void buildDashboard() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(26, 18, 26, 18);
        root.setBackgroundColor(Color.rgb(12, 17, 24));

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout timeBox = new LinearLayout(this);
        timeBox.setOrientation(LinearLayout.VERTICAL);
        clockView = text("--:--", 54, Color.WHITE, true);
        dateView = text("", 15, Color.LTGRAY, false);
        timeBox.addView(clockView);
        timeBox.addView(dateView);
        top.addView(timeBox, new LinearLayout.LayoutParams(0, -2, 1));

        TextView brand = text("YD  •  CAR MODE", 17, Color.rgb(0, 210, 190), true);
        brand.setGravity(Gravity.CENTER);
        top.addView(brand);
        root.addView(top);

        View line = new View(this);
        line.setBackgroundColor(Color.rgb(48, 59, 72));
        LinearLayout.LayoutParams lpLine = new LinearLayout.LayoutParams(-1, 1);
        lpLine.setMargins(0, 15, 0, 15);
        root.addView(line, lpLine);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER);
        addAction(actions, "MAPS", () -> openMaps());
        addAction(actions, "MUSIC", () -> openMusic());
        addAction(actions, "PHONE", () -> safeStart(new Intent(Intent.ACTION_DIAL)));
        addAction(actions, "BLUETOOTH", () -> safeStart(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS)));
        addAction(actions, "SETTINGS", () -> safeStart(new Intent(Settings.ACTION_SETTINGS)));
        root.addView(actions, new LinearLayout.LayoutParams(-1, 86));

        TextView appsTitle = text("YOUR APPS", 13, Color.rgb(0, 210, 190), true);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(-1, -2);
        titleLp.setMargins(2, 15, 0, 8);
        root.addView(appsTitle, titleLp);

        ScrollView scroll = new ScrollView(this);
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(5);
        grid.setAlignmentMode(GridLayout.ALIGN_MARGINS);
        populateApps(grid);
        scroll.addView(grid);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        TextView footer = text("Tap an app to open  •  Set “YD Car Launcher” as your Home app", 11,
                Color.rgb(133, 145, 160), false);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footLp = new LinearLayout.LayoutParams(-1, -2);
        footLp.setMargins(0, 10, 0, 0);
        root.addView(footer, footLp);

        setContentView(root);
        handler.post(clockTick);
    }

    private void addAction(LinearLayout row, String label, Runnable action) {
        Button b = tile(label, action);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, -1, 1);
        p.setMargins(4, 0, 4, 0);
        row.addView(b, p);
    }

    private void populateApps(GridLayout grid) {
        PackageManager pm = getPackageManager();
        Intent query = new Intent(Intent.ACTION_MAIN, null);
        query.addCategory(Intent.CATEGORY_LAUNCHER);
        List<android.content.pm.ResolveInfo> apps = pm.queryIntentActivities(query, 0);
        Collections.sort(apps, (a, b) -> String.valueOf(a.loadLabel(pm)).compareToIgnoreCase(String.valueOf(b.loadLabel(pm))));
        for (android.content.pm.ResolveInfo info : apps) {
            String label = String.valueOf(info.loadLabel(pm));
            String pkg = info.activityInfo.packageName;
            Button b = tile(label, () -> {
                Intent launch = pm.getLaunchIntentForPackage(pkg);
                if (launch != null) safeStart(launch);
            });
            b.setTextSize(13);
            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = getResources().getDisplayMetrics().widthPixels / 5 - 18;
            p.height = 76;
            p.setMargins(4, 4, 4, 4);
            grid.addView(b, p);
        }
    }

    private void openMaps() {
        Intent map = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="));
        map.setPackage("com.google.android.apps.maps");
        try { startActivity(map); }
        catch (ActivityNotFoundException e) {
            safeStart(new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com")));
        }
    }

    private void openMusic() {
        Intent music = new Intent(Intent.ACTION_MAIN);
        music.addCategory(Intent.CATEGORY_APP_MUSIC);
        try { startActivity(music); }
        catch (ActivityNotFoundException e) {
            new AlertDialog.Builder(this).setMessage("No music player app was found. Install or enable a music app, then try again.")
                    .setPositiveButton("OK", null).show();
        }
    }

    private void safeStart(Intent intent) {
        try { startActivity(intent); }
        catch (ActivityNotFoundException e) {
            new AlertDialog.Builder(this).setMessage("This feature is not available on this car player.")
                    .setPositiveButton("OK", null).show();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override protected void onDestroy() {
        handler.removeCallbacks(clockTick);
        super.onDestroy();
    }
}
