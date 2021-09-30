package com.thatguyalex.ltc2.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.*
import com.thatguyalex.ltc2.R
import com.thatguyalex.ltc2.viewmodel.MainViewModel

class LoginFragment : Fragment(R.layout.fragment_login), View.OnClickListener {

    private val model: MainViewModel by activityViewModels()

    private lateinit var loginButton: AppCompatButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton = view.findViewById(R.id.login_submit)
        loginButton.setOnClickListener(this)

        model.authentificated.observe(viewLifecycleOwner) { authentificated ->
            if (authentificated) {
                requireActivity().supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<GroupsFragment>(R.id.fragment_container_view)
                }
            }
        }
    }

    override fun onClick(view: View?) {
        val guid = requireView().findViewById<AppCompatEditText>(R.id.login_guid).text.toString()
        val password = requireView().findViewById<AppCompatEditText>(R.id.login_password).text.toString()
        model.guid.value = guid
        model.socsApiManager.login(guid, password)
    }
}