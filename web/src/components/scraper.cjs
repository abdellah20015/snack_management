const puppeteer = require('puppeteer');
const express = require('express');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

const PORT = 3000;

async function scrapeYoutubeVideos(searchQuery) {
  const browser = await puppeteer.launch({ headless: "new" });
  const page = await browser.newPage();

  try {
    await page.goto(`https://www.youtube.com/results?search_query=${encodeURIComponent(searchQuery)}`);
    await page.waitForSelector('ytd-video-renderer');

    const videos = await page.evaluate(() => {
      const videoElements = document.querySelectorAll('ytd-video-renderer');
      return Array.from(videoElements, element => ({
        title: element.querySelector('#video-title')?.textContent?.trim(),
        url: element.querySelector('#video-title')?.href,
        thumbnail: element.querySelector('#thumbnail img')?.src,
        channel: element.querySelector('#channel-name')?.textContent?.trim(),
        views: element.querySelector('#metadata-line span:first-child')?.textContent?.trim(),
        uploadDate: element.querySelector('#metadata-line span:last-child')?.textContent?.trim()
      })).filter(video => video.title && video.url);
    });


console.log(videos.length);
console.log(videos);

    await browser.close();
    return videos.slice(0, 12);
  } catch (error) {
    await browser.close();
    throw error;
  }
}

app.get('/api/videos/:query', async (req, res) => {
  try {
    const videos = await scrapeYoutubeVideos(req.params.query);
    res.json(videos);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
