package app.aaps.plugins.source

import android.content.Context
import android.os.Bundle
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import app.aaps.core.interfaces.receivers.Intents
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.utils.receivers.DataWorkerStorage
import app.aaps.database.entities.GlucoseValue
import app.aaps.database.impl.AppRepository
import app.aaps.database.impl.transactions.CgmSourceTransaction
import app.aaps.core.interfaces.utils.DateUtil
import app.aaps.shared.impl.utils.DateUtilImpl
import app.aaps.shared.tests.TestBase
import com.google.common.truth.Truth.assertThat
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

class XdripSourcePluginTest : TestBase() {

    abstract class ContextWithInjector : Context(), HasAndroidInjector

    private lateinit var xdripSourcePlugin: XdripSourcePlugin
    private lateinit var dateUtil: DateUtil
    private lateinit var dataWorkerStorage: DataWorkerStorage

    private val injector = HasAndroidInjector { AndroidInjector { } }

    @Mock lateinit var rh: ResourceHelper
    @Mock lateinit var context: ContextWithInjector

    @BeforeEach
    fun setup() {
        `when`(context.applicationContext).thenReturn(context)
        `when`(context.androidInjector()).thenReturn(injector.androidInjector())
        xdripSourcePlugin = XdripSourcePlugin(injector, rh, aapsLogger)
        dateUtil = DateUtilImpl(context)
        dataWorkerStorage = DataWorkerStorage(context)
    }

    @Test fun advancedFilteringSupported() {
        assertThat(xdripSourcePlugin.advancedFilteringSupported()).isFalse()
    }

    @Test
    fun doWorkAndLog() = runTest {
        val timestamp = dateUtil.now()
        val bgEstimate = 175.0
        val trendArrow = GlucoseValue.TrendArrow.FLAT
        val sourceSensor = GlucoseValue.SourceSensor.LIBRE_2_NATIVE

        xdripSourcePlugin.setPluginEnabled(xdripSourcePlugin.pluginDescription.mainType, true)
        val bundle = Bundle()
        bundle.putLong(Intents.EXTRA_TIMESTAMP, timestamp)
        bundle.putDouble(Intents.EXTRA_BG_ESTIMATE, bgEstimate)
        bundle.putString(Intents.EXTRA_BG_SLOPE_NAME, trendArrow.text)
        bundle.putString(Intents.XDRIP_DATA_SOURCE_DESCRIPTION, sourceSensor.text)

        lateinit var worker: XdripSourcePlugin.XdripSourceWorker
        val sut = TestListenableWorkerBuilder<XdripSourcePlugin.XdripSourceWorker>(context)
            .setWorkerFactory(object: WorkerFactory() {
                override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): XdripSourcePlugin.XdripSourceWorker {
                    worker = XdripSourcePlugin.XdripSourceWorker(context, workerParameters)
                    worker.dataWorkerStorage = dataWorkerStorage
                    return worker
                }
            })
            .setInputData(dataWorkerStorage.storeInputData(bundle, Intents.ACTION_NEW_BG_ESTIMATE)).build()

        val repositorySpy = spy(worker.repository)
        lateinit var single: Single<CgmSourceTransaction.TransactionResult>
         doAnswer { invocation: InvocationOnMock ->
            single = invocation.callRealMethod() as Single<CgmSourceTransaction.TransactionResult>
            single
        }.`when`(repositorySpy).runTransactionForResult<CgmSourceTransaction.TransactionResult>(any())
        worker.repository = repositorySpy

        sut.doWorkAndLog()

        verify(repositorySpy, times(1)).runTransactionForResult<CgmSourceTransaction.TransactionResult>(any())
        single.blockingGet().also { result ->
            val glucoseValues = result.all()
            assertThat(glucoseValues).hasSize(1)
            val value = glucoseValues[0]
            assertThat(value.value).isEqualTo(bgEstimate)
            assertThat(value.timestamp).isEqualTo(timestamp)
            assertThat(value.sourceSensor).isEqualTo(sourceSensor)

            assertThat(result.sensorInsertionsInserted).isEmpty()
        }
    }
}
