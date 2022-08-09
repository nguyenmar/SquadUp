package com.ancientones.squadup.dropin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ancientones.squadup.databinding.ActivityMemberListBinding
import com.ancientones.squadup.ui.dropin.MemberListAdapter

class DropInMembersActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMemberListBinding
    private lateinit var memberList: List<String>
    private lateinit var memberListAdapter: MemberListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMemberListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        memberList = intent.getStringArrayListExtra("memberList")!!
        memberListAdapter = MemberListAdapter(this, memberList)
        binding.listView.adapter = memberListAdapter
    }
}