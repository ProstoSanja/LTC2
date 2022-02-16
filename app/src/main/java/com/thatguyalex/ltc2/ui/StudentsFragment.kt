package com.thatguyalex.ltc2.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.thatguyalex.ltc2.BarcodeAnalyzer
import com.thatguyalex.ltc2.R
import com.thatguyalex.ltc2.ui.adapters.StudentsAdapter
import com.thatguyalex.ltc2.viewmodel.MainViewModel
import java.util.concurrent.Executors

class StudentsFragment : Fragment(R.layout.fragment_students) {

    private val model: MainViewModel by activityViewModels()

    private lateinit var loadingBar: LinearProgressIndicator
    private lateinit var studentsRecycler: RecyclerView
    private lateinit var barcodeButton: MaterialButton
    private lateinit var viewFinder: PreviewView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = StudentsAdapter(emptyList(), requireContext(), this::setAttendance, this::deleteAttendance)

        viewFinder = view.findViewById(R.id.viewFinder)
        loadingBar = view.findViewById(R.id.students_loading)

        barcodeButton = view.findViewById(R.id.student_scan_button)
        barcodeButton.setOnClickListener {
            barcodeButton.visibility = View.GONE
            viewFinder.visibility = View.VISIBLE
            tryStartCamera()
        }

        studentsRecycler = view.findViewById(R.id.students_recycler_view)
        studentsRecycler.layoutManager = LinearLayoutManager(context)
        studentsRecycler.adapter = adapter

        model.selectedGroup.observe(viewLifecycleOwner){
            requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).title = it.courseName + " " + it.groupName
            loadingBar.visibility = View.VISIBLE
            model.socsApiManager.fetchGroup(it)
        }

        model.selectedGroupStudentsList.observe(viewLifecycleOwner){
            if (it.isEmpty()) {
                return@observe
            }
            loadingBar.visibility = View.GONE
            adapter.updateDataSet(it)
        }

    }

    override fun onDestroyView() {
        model.selectedGroupStudentsList.value = emptyList()
        super.onDestroyView()
    }

    fun setAttendance(barcode: String, reportError : Boolean) {
        model.socsApiManager.submitAttendance(barcode, model.selectedGroup.value!!.courseName, model.selectedGroup.value!!.courseId, reportError) {
            attendanceCallback(barcode, true)
        }
    }

    fun deleteAttendance(barcode: String) {
        model.socsApiManager.deleteAttendance(barcode, model.selectedGroup.value!!.courseName, model.selectedGroup.value!!.courseId) {
            attendanceCallback(barcode, false)
        }
    }

    fun attendanceCallback(barcode: String, created: Boolean) {
        val student = model.selectedGroupStudentsList.value?.find { it.barcode == barcode }
        val callbackValue = student?.name ?: barcode
        Snackbar.make(requireView(), (if (created) "Registered attendance: " else "Deleted attendance: ") + callbackValue, BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(Executors.newSingleThreadExecutor(), BarcodeAnalyzer(this::setAttendance))
                }

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer)
            } catch (exc: Exception) {
                model.handleApiError(exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    fun tryStartCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(CAMERA_PERMISSION),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            barcodeButton.visibility = View.VISIBLE
            viewFinder.visibility = View.GONE
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    }

}