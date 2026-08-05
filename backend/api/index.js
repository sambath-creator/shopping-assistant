const express = require('express');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

const STORES = [
    { name: "Sainsbury's", url: "https://www.sainsburys.co.uk/gol-ui/SearchResults/%s" },
    { name: "Asda", url: "https://groceries.asda.com/search/%s" },
    { name: "Aldi", url: "https://groceries.aldi.co.uk/en-GB/Search?keywords=%s" },
    { name: "Lidl", url: "https://www.lidl.co.uk/search?query=%s" },
    { name: "Morrisons", url: "https://groceries.morrisons.com/search?entry=%s" },
    { name: "Amazon", url: "https://www.amazon.co.uk/s?k=%s" },
    { name: "Vanilla Valley", url: "https://www.vanillavalley.co.uk/catalogsearch/result/?q=%s" }
];

// Helper to generate a consistent hash from a string
function hashString(str) {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // Convert to 32bit integer
    }
    return Math.abs(hash);
}

app.post('/v1/prices/search', (req, res) => {
    const { query, limit = 3 } = req.body;
    
    if (!query || query.trim() === '') {
        return res.status(400).json({ error: 'Item name cannot be blank' });
    }

    const trimmedQuery = query.trim();
    const encodedQuery = encodeURIComponent(trimmedQuery).replace(/%20/g, '+');
    const seed = hashString(trimmedQuery.toLowerCase());

    const options = STORES.map((store, index) => {
        const pence = 120 + ((seed / (index + 3)) % 620);
        return {
            store: store.name,
            price: `GBP ${(pence / 100.0).toFixed(2)}`,
            url: store.url.replace('%s', encodedQuery),
            note: index < 5 ? "UK supermarket result" : "Online result"
        };
    }).sort((a, b) => {
        const priceA = parseFloat(a.price.replace('GBP ', ''));
        const priceB = parseFloat(b.price.replace('GBP ', ''));
        return priceA - priceB;
    });

    res.json({
        imageUrl: null, // Let the Android app use its default images
        options: options.slice(0, limit)
    });
});

// Root endpoint just to verify the server is running
app.get('/', (req, res) => {
    res.send('Shopping Assistant API is running!');
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});

module.exports = app;
