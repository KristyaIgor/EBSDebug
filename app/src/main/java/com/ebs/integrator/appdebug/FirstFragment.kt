package com.ebs.integrator.appdebug

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.ebs.integrator.appdebug.databinding.FragmentFirstBinding
import com.ebs.integrator.ebsdebug.EBSDebug
import com.ebs.integrator.ebsdebug.enums.EbsLevel
import com.ebs.integrator.ebsdebug.logger.LogsRepository
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var api: RemoteApiInterface? = null
    lateinit var ebsDebug : EBSDebug

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        ebsDebug = EBSDebug(requireActivity())

        val intercept = ebsDebug.getInterceptor()
            .setLevelInterceptor(EbsLevel.Body)
            .build()

        val retrofit = Retrofit.Builder().client(OkHttpClient.Builder().addNetworkInterceptor(intercept).build()).baseUrl("http://edi.md:4444/").addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().serializeNulls().create()
            )
        ).build()
        api = retrofit.create(RemoteApiInterface::class.java)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            lifecycleScope.launch {
                try {
                    val ping = api?.ping()
                    if (ping != null) {
                        if(ping.isSuccessful)
                            Log.e("TAG", "onViewCreated: $ping" )
                    }
                }catch (e: Exception){
                    Log.e("TAG", "Exception: ${e.message}" )
                }
            }
        }

        binding.button.setOnClickListener {
            binding.textView.text = ""
            lifecycleScope.launch(Dispatchers.Main){

                val repo = LogsRepository(requireActivity())
               val resp =  repo.getRequestsModels().toMutableList()
                binding.button.text = binding.button.text.toString() + ": " + resp.size
                resp.forEach{
                    binding.textView.append("\n" + it.toString())
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}