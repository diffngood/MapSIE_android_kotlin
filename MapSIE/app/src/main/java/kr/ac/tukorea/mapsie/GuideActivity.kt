package kr.ac.tukorea.mapsie

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.main_drawer_header.*
import kotlinx.android.synthetic.main.main_toolbar.*
import kr.ac.tukorea.mapsie.databinding.ActivityAddBinding
import kr.ac.tukorea.mapsie.databinding.ActivityGuideBinding
import java.text.SimpleDateFormat
import java.util.*

class GuideActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityGuideBinding

    var db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding 방식으로 변경
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //왼쪽에 뒤로가기버튼생성
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게
        toolbar.title = "MapSIE"
        binding.navigationView.setNavigationItemSelectedListener(this)

        db.collection("users").document(Firebase.auth.currentUser?.uid ?: "No User").get().addOnSuccessListener {
            member_nickname.text = it["signName"].toString()
            Glide.with(this)
                .load(it["signImg"])
                .override(60, 60)
                .error(R.drawable.ic_baseline_account_circle_24)    //에러가 났을 때
                .fallback(R.drawable.ic_baseline_account_circle_24) //signImg값이 없다면 기본 사진 출력
                .into(member_icon)
        }.addOnFailureListener {
            Toast.makeText(this, ".", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)       //툴바 메뉴
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {   //툴바에 있는 메뉴 누를 때
        when (item.itemId) {
            android.R.id.home -> {   //뒤로가기 버튼
                finish()
                return true
            }
            R.id.toolbar_menu -> { // 메뉴 버튼
                drawer_layout.openDrawer(GravityCompat.END)    // 네비게이션 드로어 열기(오른쪽에서 왼쪽으로)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {  //기본 폰에 내장되어 있는 ◀뒤로가기 누르면
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawers()
            // 테스트를 위해 뒤로가기 버튼시 Toast 메시지
            Toast.makeText(this, "뒤로가기버튼 테스트", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {    //메뉴바 클릭 시 실행하는 메서드
        when (item.itemId) {
            R.id.home -> startActivity(Intent(this, MainActivity::class.java))
            R.id.mypage-> startActivity(Intent(this, MyPageActivity::class.java))
            R.id.guideline-> startActivity(Intent(this, GuideActivity::class.java))
            R.id.addPage -> startActivity(Intent(this, AddActivity::class.java))
            R.id.logout -> {
                val builder = AlertDialog.Builder(this)
                    .apply {
                        setTitle("알림")
                        setMessage("로그아웃 하시겠습니까?")
                        setPositiveButton("네") { _, _ ->
                            FirebaseAuth.getInstance().signOut()
                            Handler().postDelayed({
                                ActivityCompat.finishAffinity(this@GuideActivity)
                                System.runFinalization()
                                System.exit(0)
                            }, 1000)
                        }
                        setNegativeButton("아니요") { _, _, ->
                            return@setNegativeButton
                        }
                        show()
                    }
            }
        }
        return false
    }
}
