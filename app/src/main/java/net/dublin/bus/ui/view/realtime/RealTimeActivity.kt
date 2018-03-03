package net.dublin.bus.ui.view.realtime

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_real_time.*
import net.dublin.bus.R
import net.dublin.bus.data.realtime.repository.RealTimeRepository
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Stop
import net.dublin.bus.model.StopData
import net.dublin.bus.ui.utilities.Utility

class RealTimeActivity : AppCompatActivity(), RealTimeAdapter.ItemClickListener, RealTimeContract.View, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var presenter: RealTimeContract.Presenter
    private var mAdapter: RealTimeAdapter? = null
    private var stopNumber: String = ""
    private var description: String = ""
    private var snackBarNoConnection: Snackbar? = null
    private var snackBarError: Snackbar? = null
    private var menuFavourite: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time)
        initExtra()
        setupRecyclerView()
        setupView()
        initialize()
    }

    private fun initExtra() {
        stopNumber = intent.getStringExtra(EXTRA_STOP_NUMBER)
        description = intent.getStringExtra(EXTRA_DESCRIPTION)
    }

    private fun setupView() {
        stop_description_aux_view.text = stopNumber
        stop_description_view.text = description
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        real_swipe_refresh_layout.setOnRefreshListener(this)
    }

    private fun setupRecyclerView() {
        mAdapter = RealTimeAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAdapter
        recyclerView.isNestedScrollingEnabled = false
    }

    private fun initialize() {
        presenter = RealTimePresenter(this,
                RealTimeRepository(),
                StopRepository(application),
                stopNumber, description)
    }

    override fun onResume() {
        super.onResume()
        presenter.loadData()
    }

    public override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    override fun showLineNote(lineNote: String) {
        real_line_note_view.visibility = View.VISIBLE
        real_line_note_view.text = lineNote
    }

    override fun hideLineNote() {
        real_line_note_view.visibility = View.GONE
    }

    override fun getSizeData(): Int {
        return mAdapter?.itemCount ?: 0
    }

    override fun onRefresh() {
        presenter.loadData()
    }

    override fun showProgress() {
        real_progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        real_progress_bar.visibility = View.GONE
    }

    override fun showProgressSwipe() {
        real_swipe_refresh_layout.isRefreshing = true
    }

    override fun hideProgressSwipe() {
        real_swipe_refresh_layout.isRefreshing = false
    }

    override fun showSnackBarNoConnection() {
        snackBarNoConnection = Snackbar.make(
                real_swipe_refresh_layout,
                R.string.title_no_connection,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.title_retry) { presenter.loadData() }

        snackBarNoConnection?.show()
    }

    override fun showSnackBarError() {
        snackBarError = Snackbar.make(
                real_swipe_refresh_layout,
                R.string.error_message,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.title_retry) { presenter.loadData() }

        snackBarError?.show()
    }

    override fun hideSnackBar() {
        snackBarNoConnection?.dismiss()
        snackBarError?.dismiss()
    }

    override fun showNoData() {
        real_message.visibility = View.VISIBLE
    }

    override fun hideNoData() {
        real_message.visibility = View.GONE
    }

    override fun showData(data: List<StopData>) {
        mAdapter?.replaceData(data)
    }

    override fun isNetworkAvailable(): Boolean {
        return Utility.isNetworkAvailable(this)
    }

    override fun onItemClick(item: String, view: ImageView) {
    }

    override fun showFavouriteYes() {
        menuFavourite?.let {
            it.isVisible = true
            it.setIcon(R.drawable.ic_favorite_white_24dp)
        }
    }

    override fun showFavouriteNo() {
        menuFavourite?.let {
            it.isVisible = true
            it.setIcon(R.drawable.ic_favorite_border_white_24dp)
        }
    }

    override fun showSnackbarRemoveFavourite() {
        Snackbar.make(real_swipe_refresh_layout, R.string.title_remove_favourite, Snackbar.LENGTH_SHORT).show()
    }

    override fun showSnackbarSaveFavourite() {
        Snackbar.make(real_swipe_refresh_layout, R.string.title_add_favourite, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.real_time, menu)
        menuFavourite = menu.findItem(R.id.menu_favorite)
        menuFavourite?.isVisible = false
        presenter.loadFavouriteStatus()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_favorite -> {
                presenter.addOrRemoveFavourite()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_STOP_NUMBER = "stop_stopNumber"
        const val EXTRA_DESCRIPTION = "stop_description"

        fun navigate(context: Context, item: Stop) {
            val intent = Intent(context, RealTimeActivity::class.java)
            intent.putExtra(EXTRA_STOP_NUMBER, item.stopNumber)
            intent.putExtra(EXTRA_DESCRIPTION, item.descriptionOrAddress())
            context.startActivity(intent)
        }
    }
}
