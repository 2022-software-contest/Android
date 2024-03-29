package com.example.ssu_contest_eighteen_pomise.mainfragments

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ssu_contest_eighteen_pomise.App
import com.example.ssu_contest_eighteen_pomise.R
import com.example.ssu_contest_eighteen_pomise.databinding.FragmentHomeBinding
import com.example.ssu_contest_eighteen_pomise.mainfragments.list.DetailAlarmActivity
import com.example.ssu_contest_eighteen_pomise.mainfragments.list.PillAlarmListAdapter
import com.example.ssu_contest_eighteen_pomise.mainfragments.patient_list.PatientListAdapter
import com.yourssu.design.system.atom.ToolTip
import com.yourssu.design.system.component.Toast.Companion.shortToast
import com.yourssu.design.undercarriage.size.dpToIntPx


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeFragmentViewModel by viewModels()
    private val patientListAdapter = PatientListAdapter()
    private val adapter = PillAlarmListAdapter()
    var tooltipBuilders: ToolTip.Builder? = null
    lateinit var wd: WindowManager
    lateinit var d: Display
    private val metrics = DisplayMetrics()
    private var mWidthPixels = 0 // 화면 정보를 얻음
    private var mHeightPixels = 0 // 화면 정보를 얻음
    private var mWidthPatientItems = 0 // 환자 아이템 하나의 너비.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this@HomeFragment
        binding.viewModel = viewModel

        //툴팁 빌더 초기화.
        context?.let {
            activity?.let { it1 ->
                tooltipBuilders =
                    ToolTip.Builder(context = it, it1.windowManager, inflater)
                wd = it1.windowManager
                d = wd.defaultDisplay as Display
                d.getMetrics(metrics) //현재 화면의 크기 구함.
                mWidthPixels = metrics.widthPixels //현재화면의 너비 픽셀을 구함. 이것만 있으면 됨. 우리는.
                mHeightPixels = metrics.heightPixels
            }
        }
        mWidthPatientItems = context?.dpToIntPx(120f) ?: 0 //환자 아이템 하나의 너비의 픽셀값을 구함.
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.patientListRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.patientListRv.adapter = patientListAdapter

        //binding.patientListRv.verticalScrollbarPosition=View.SCROLLBAR_POSITION_LEFT
        binding.pillListRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.pillListRv.adapter = adapter
        binding.refreshLayout.setColorSchemeColors(resources.getColor(R.color.mainColor))

        onViewModelInit()

        onInitView()
    }

    fun onViewModelInit() {
        viewModel.isGuardianLiveData.observe(viewLifecycleOwner, {
            // 초기화 작업...
            if (it) {

            } else {
                binding.patientListRv.visibility = View.GONE
            }
        })

        viewModel.refreshStartEvent.observe(viewLifecycleOwner, {
            binding.refreshLayout.isRefreshing = true
        })

        viewModel.patientListItems.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                patientListAdapter.submitList(it)
            } else {
                binding.patientListRv.visibility=View.GONE
                binding.nameTopBar.visibility=View.GONE
                binding.emptyPatientListExplainText.visibility=View.VISIBLE
            }
        })

        viewModel.curPatientEmail.observe(viewLifecycleOwner, {
            viewModel.getListItem(true)
        })

        viewModel.pillListItems.observe(viewLifecycleOwner, {
            adapter.updateItems(it ?: emptyList())
            if (it.isEmpty()) {
                binding.pillListRv.isVisible = false
                binding.emptyListExplainText.visibility = View.VISIBLE
            } else {
                binding.pillListRv.isVisible = true
                binding.emptyListExplainText.visibility = View.GONE
            }
        })

        viewModel.refreshEndEvent.observe(viewLifecycleOwner, {
            binding.refreshLayout.isRefreshing = false
            shortToast("알람목록을 갱신했습니다.")
        })

        viewModel.refreshInitWithViewModel.observe(viewLifecycleOwner, {
            binding.refreshLayout.isRefreshing = false
        })

        viewModel.refreshFailedEvent.observe(viewLifecycleOwner, {
            binding.refreshLayout.isRefreshing = false
        })
    }

    fun onInitView() {
        adapter.setMyItemClickListener(object : PillAlarmListAdapter.MyItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(activity, DetailAlarmActivity::class.java)
                intent.putExtra(
                    DetailAlarmActivity.KEY_PILL_NAME,
                    viewModel.pillListItems.value?.elementAt(position)
                )
                startActivity(intent)
            }

            override fun onMorePillInfoClick(position: Int, view: View) {
                val toolTip: ToolTip =
                    tooltipBuilders
                        ?.withIsNormal(true)
                        ?.withStringContents(viewModel.tooltipString(position))
                        ?.withHopeLocation(ToolTip.HopeLocation.BELOW)
                        ?.withToastLength(
                            ToolTip.Length.LONG
                        )!!.build(view)

                toolTip.show()
            }

            override fun onItemLongClick(position: Int) {

            }
        })
        binding.pillListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_SETTLING
                    && !recyclerView.canScrollVertically(1)
                ) {
                    shortToast(resources.getString(R.string.can_not_load_more_alarm_list_toast_message))
                }
            }
        })

        patientListAdapter.setMyItemClickListener(object : PatientListAdapter.MyItemClickListener {
            override fun onSelectedItemClick(position: Int, email: String) {
                val offset = mWidthPixels / 2 - mWidthPatientItems / 2
                val layoutManager = binding.patientListRv.layoutManager as LinearLayoutManager
                layoutManager.scrollToPositionWithOffset(position, offset)
            }

            override fun onNonSelectedItemClick(position: Int, email: String) {
                viewModel.clickPatientIndex(position, email)
                val offset = mWidthPixels / 2 - mWidthPatientItems / 2
                val layoutManager = binding.patientListRv.layoutManager as LinearLayoutManager
                layoutManager.scrollToPositionWithOffset(position, offset)
            }
        })
    }

    override fun onStart() {
        val shPre = App.token_prefs
        if (shPre.isGuardian != true) {
            viewModel.setMyData()
        }
        super.onStart()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}