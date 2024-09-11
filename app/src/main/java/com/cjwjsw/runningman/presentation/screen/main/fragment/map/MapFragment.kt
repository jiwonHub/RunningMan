package com.cjwjsw.runningman.presentation.screen.main.fragment.map

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cjwjsw.runningman.core.DataSingleton
import com.cjwjsw.runningman.databinding.FragmentMapBinding
import com.cjwjsw.runningman.service.LocationUpdateService
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var locationSource: FusedLocationSource
    private val viewModel: MapViewModel by viewModels()
    private var polyline: PolylineOverlay? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        startLocationService()
        observeData()

        // 터치 리스너 설정
        mapView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    binding.scrollView.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP -> {
                    binding.scrollView.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        binding.resetButton.setOnClickListener {
            viewModel.resetData()

            polyline?.map = null
            polyline = PolylineOverlay().apply {
                coords = emptyList()
                map = null // 지우기 위해 null로 설정
            }
        }

        requireActivity().startService(LocationUpdateService.newIntent(requireContext()))
    }

    private fun observeData() {
        viewModel.address.observe(viewLifecycleOwner) { address ->
            binding.locationText.text = address
        }
        viewModel.distance.observe(viewLifecycleOwner) { distance ->
            binding.distance.text = distance.toString()
        }
    }

    private fun startLocationService() {
        requireActivity().startService(LocationUpdateService.newIntent(requireContext()))
    }

    override fun onMapReady(naverMap: NaverMap) {
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        binding.currentLocationButton.map = naverMap
        naverMap.locationSource = locationSource
        val uiSetting = naverMap.uiSettings // 현위치 버튼
        uiSetting.isLocationButtonEnabled = false

        naverMap.maxZoom = 18.0 // 최대 줌
        naverMap.minZoom = 10.0 // 최소 줌

        viewModel.path.observe(viewLifecycleOwner) { path ->
            polyline?.map = null // 기존 폴리라인 제거
            if (path.size > 1) {
                polyline = PolylineOverlay().apply {
                    coords = path
                    color = Color.RED
                    map = naverMap // 새로운 폴리라인 추가
                }
            }
        }

        // 위치 업데이트 콜백 설정
        naverMap.addOnLocationChangeListener { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            viewModel.addLocation(latLng, requireContext())
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
//        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
//        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
//        mapView.onLowMemory()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}