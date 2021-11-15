package com.hover.runner.parser.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hover.runner.parser.model.Parser
import com.hover.runner.parser.viewmodel.usecase.ParserUseCase
import com.hover.runner.transactions.model.RunnerTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ParserViewModel(private val useCase: ParserUseCase) : ViewModel() {
     val parserLiveData: MutableLiveData<Parser> = MutableLiveData()
     val transactionsLiveData: MutableLiveData<List<RunnerTransaction>> = MutableLiveData()

    fun getParser(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            parserLiveData.postValue(useCase.getParser(id))
        }
    }

    fun getTransactions(parserId: Int) {
       viewModelScope.launch(Dispatchers.IO) {
           transactionsLiveData.postValue(useCase.getTransactions(parserId))
       }
    }

}