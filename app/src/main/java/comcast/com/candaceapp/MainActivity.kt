package comcast.com.candaceapp

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.io.File
import android.graphics.drawable.Drawable




class MainActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private var currentlyPlayingStreamId: Int? = null

    val soundPool = SoundPool(5, AudioManager.STREAM_MUSIC, 0).apply {
        setOnLoadCompleteListener { soundPool, streamId, status ->
            currentlyPlayingStreamId = streamId
            soundPool.play(streamId, 1f, 1f, 0, 0, 1f)
        }
    }

    private fun playSound(context: Context, soundPool: SoundPool, soundResourceInt: Int) {
        if (soundResourceInt != NO_SOUND) {
            soundPool.load(context, soundResourceInt, 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageSelected(position: Int) {
                currentlyPlayingStreamId?.apply {
                    soundPool.stop(this)
                }

                var soundResourceInt = soundResourceArray[position]

                if (soundResourceInt == RANDOM_SOUND) {
                    soundResourceInt = randomSoundArray[(0..(randomSoundArray.size-1)).random()]
                }

                if (soundResourceInt != NO_SOUND) {
                    soundPool.load(this@MainActivity, soundResourceInt, 1)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return imageNameArray.size
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            val rootView = inflater.inflate(R.layout.fragment_main, container, false)

            rootView.mainWebView.isVerticalScrollBarEnabled = false
            rootView.mainWebView.isHorizontalScrollBarEnabled = false

            rootView.mainWebView.setBackgroundColor(Color.TRANSPARENT)


            var imageFile = arguments?.getString(ARG_PAGE_IMAGE_NAME)!!

            if (imageFile.endsWith(".gif")) {
                var url = "${arguments?.getString(ARG_PAGE_IMAGE_NAME)}"
                var data = "<html><head><title>Example</title><meta name=\"viewport\" \"content=\"width=960, initial-scale=.70 \" /></head><body><center><img width=\"960\" src=\"$url\" /></center></body></html>"

                rootView.mainWebView.loadDataWithBaseURL("file:///android_asset/", data, "text/html", "UTF-8",null)

                rootView.mainWebView.visibility = View.VISIBLE
                rootView.mainImageView.visibility = View.GONE
            } else {

                val ims = activity?.assets?.open(imageFile)
                val d = Drawable.createFromStream(ims, null)
                rootView.mainImageView.setImageDrawable(d)
                ims?.close()

                rootView.mainWebView.visibility = View.GONE
                rootView.mainImageView.visibility = View.VISIBLE
            }

            return rootView
        }

        override fun onPause() {
            super.onPause()
            view?.mainWebView?.stopLoading()
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_PAGE_IMAGE_NAME = "argPageImageName"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(pageIndex: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putString(ARG_PAGE_IMAGE_NAME, imageNameArray[pageIndex])
                fragment.arguments = args
                return fragment
            }
        }
    }

    companion object {

        val NO_SOUND = -1
        val RANDOM_SOUND = -2

        // NO MORE THAN 5 for now!
        var imageNameArray = arrayOf("fake_xhui_home.png", "jet_flying.jpg", "xhui_group.jpg", "fidget.gif")
        var soundResourceArray = arrayOf(NO_SOUND, R.raw.jet_flying, RANDOM_SOUND, NO_SOUND)
        var randomSoundArray = arrayOf(R.raw.ajay, R.raw.bryan, R.raw.jin, R.raw.me, R.raw.rachael, R.raw.song, R.raw.jeremy, R.raw.pri, R.raw.sreekanth, R.raw.khevna)
    }
}
