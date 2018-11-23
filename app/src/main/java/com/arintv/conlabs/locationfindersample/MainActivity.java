package com.arintv.conlabs.locationfindersample;

// GPS 권한을 통해 위치를 체크하는 애플리케이션
// 2018.11.23
// 2018 (c) ARINTV Contents Creation Labs.
// 이 소스는 Google의 공식 가이드인 "런타임에 권한 요청"(https://developer.android.com/training/permissions/requesting)
// 마시멜로우 버전 이상에서 권한 얻어오기, 안싱미("http://ande226.tistory.com/136")을 참고하여 만들어진 공개 예제입니다.

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView permissionStatus;
    Button permissionButton;

    TextView xPos;
    TextView yPos;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionStatus = findViewById(R.id.permissionCheckView);
        permissionButton = findViewById(R.id.GotoPermissionBtn);

        xPos = findViewById(R.id.xPostView);
        yPos = findViewById(R.id.yPostView);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // 경도: xPos
                // 위도: yPos
                xPos.setText("xPos:" + location.getLongitude());
                yPos.setText("yPos:" + location.getLatitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    // onStart에 체크하는 이유는 권한을 매 실행시마다 체크
    @Override
    protected void onStart() {
        super.onStart();
        permissionStatus = findViewById(R.id.permissionCheckView);
        // 사용자에게 권한을 요청하기 시작한 버전은 6.0 (마시멜로우) 이상이다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 위치정보 권한 체크: 권한이 거부되어 있을 경우
            int permissionValue = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionValue == PackageManager.PERMISSION_DENIED) {
                // 권한을 거부한 적이 있다면 다이얼로그 상자로 권한이 없다고 메시지를 출력한다.
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setTitle("권한 필요");
                    alertBuilder.setMessage("위치 권한이 활성화되어 있어야 합니다.");
                    alertBuilder.setPositiveButton("권한 설정", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                                // 권한 요청하기, requestCode는 Intent와 마찬가지로 임의로 부여하는 값이다.
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                            }
                        }
                    });
                    alertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "권한 요청을 거부하였습니다. \n애플리케이션이 작동하지 않을 수 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
                    // 처음 권한을 요청하는 경우
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                }
            } else {
                permissionStatus.setTextColor(Color.GREEN);
                permissionStatus.setText("권한 활성화");
            }
        } else {
            permissionStatus.setTextColor(Color.YELLOW);
            permissionStatus.setText("6.0 버전 이하입니다.");
        }
    }

    // 권한이 체크되었는지 체크하는 오버라이딩 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionStatus.setTextColor(Color.GREEN);
                permissionStatus.setText("권한 활성화");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // 권한이 활성화되었다면 실행할 인텐트 결정
                }
            }
        } else {
            Toast.makeText(MainActivity.this, "권한 요청을 거부하였습니다. \n애플리케이션이 작동하지 않을 수 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // XML에 지정된 onClick 속성에 입력한 값. 버튼을 누르면 이 애플리케이션의 설정 화면으로 넘어간다.
    protected void applicationSetting(View view) {
        Intent openSetting = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        openSetting.setData(Uri.parse("package:" + getPackageName()));
        if (openSetting.resolveActivity(getPackageManager()) != null) {
            startActivity(openSetting);
        }
    }
}







