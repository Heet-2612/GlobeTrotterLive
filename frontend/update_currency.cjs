const fs = require('fs');

function replaceInFile(filePath, replacements) {
    let content = fs.readFileSync(filePath, 'utf8');
    for (const [regex, replacement] of replacements) {
        content = content.replace(regex, replacement);
    }
    fs.writeFileSync(filePath, content);
}

// 1. ItineraryBuilderPage.tsx
replaceInFile('src/pages/ItineraryBuilderPage.tsx', [
    [/import \{ Button, Card, Input, LoadingState \} from '\.\.\/components\/common\/UIComponents';/, "import { Button, Card, Input, LoadingState } from '../components/common/UIComponents';\nimport { formatCurrency } from '../utils/currencyUtils';"],
    [/<span>Budget: \$\{trip\.budget \? trip\.budget\.toLocaleString\(\) : '0'\}<\/span>/g, "<span>Budget: {trip.budget ? formatCurrency(trip.budget) : formatCurrency(0)}</span>"],
    [/\$\{act\.customCost \?\? act\.activity\.estimatedCost \?\? 0\}/g, "{formatCurrency(act.customCost ?? act.activity.estimatedCost ?? 0)}"],
    [/>\$\{act\.estimatedCost \|\| 0\}<\/span>/g, ">{formatCurrency(act.estimatedCost || 0)}</span>"],
    [/\(\$\{selectedActivity\.estimatedCost \|\| 0\}\)/g, "({formatCurrency(selectedActivity.estimatedCost || 0)})"],
    [/label="Custom Cost \(\$\)"/g, 'label="Custom Cost (INR ₹)"']
]);

// 2. ItineraryViewPage.tsx
replaceInFile('src/pages/ItineraryViewPage.tsx', [
    [/import \{ Button, LoadingState \} from '\.\.\/components\/common\/UIComponents';/, "import { Button, LoadingState } from '../components/common/UIComponents';\nimport { formatCurrency } from '../utils/currencyUtils';"],
    [/<span>\$\{trip\.budget \? trip\.budget\.toLocaleString\(\) : '0'\}<\/span>/g, "<span>{trip.budget ? formatCurrency(trip.budget) : formatCurrency(0)}</span>"],
    [/\$\{act\.customCost \?\? act\.activity\.estimatedCost \?\? 0\}/g, "{formatCurrency(act.customCost ?? act.activity.estimatedCost ?? 0)}"]
]);

// 3. SharedItineraryPage.tsx
replaceInFile('src/pages/SharedItineraryPage.tsx', [
    [/import \{ Button, LoadingState \} from '\.\.\/components\/common\/UIComponents';/, "import { Button, LoadingState } from '../components/common/UIComponents';\nimport { formatCurrency } from '../utils/currencyUtils';"],
    [/<span>\$\{itinerary\.budget \? itinerary\.budget\.toLocaleString\(\) : '0'\}<\/span>/g, "<span>{itinerary.budget ? formatCurrency(itinerary.budget) : formatCurrency(0)}</span>"],
    [/>\$\{act\.cost\}<\/span>/g, ">{formatCurrency(act.cost)}</span>"]
]);

// 4. TimelinePage.tsx
replaceInFile('src/pages/TimelinePage.tsx', [
    [/import \{ Button, LoadingState \} from '\.\.\/components\/common\/UIComponents';/, "import { Button, LoadingState } from '../components/common/UIComponents';\nimport { formatCurrency } from '../utils/currencyUtils';"],
    [/\$\{act\.customCost \?\? act\.activity\.estimatedCost \?\? 0\}/g, "{formatCurrency(act.customCost ?? act.activity.estimatedCost ?? 0)}"]
]);

// 5. CreateTripPage.tsx
replaceInFile('src/pages/CreateTripPage.tsx', [
    [/label="Target Budget \(\$ USD\)"/g, 'label="Target Budget (INR ₹)"']
]);

// 6. DashboardPage.tsx
replaceInFile('src/pages/DashboardPage.tsx', [
    [/import \{ Button, LoadingState \} from '\.\.\/components\/common\/UIComponents';/, "import { Button, LoadingState } from '../components/common/UIComponents';\nimport { formatCurrency } from '../utils/currencyUtils';"],
    [/>\$\{totalBudget\.toLocaleString\(\)\}<\/p>/g, ">{formatCurrency(totalBudget)}</p>"]
]);

// 7. AdminDashboardPage.tsx
replaceInFile('src/pages/AdminDashboardPage.tsx', [
    [/import \{ Button, LoadingState \} from '\.\.\/components\/common\/UIComponents';/, "import { Button, LoadingState } from '../components/common/UIComponents';\nimport { formatCurrency } from '../utils/currencyUtils';"],
    [/>\$\{stats\.totalBudget\}<\/p>/g, ">{formatCurrency(stats.totalBudget)}</p>"],
    [/>\$\{stats\.avgTripBudget\}<\/p>/g, ">{formatCurrency(stats.avgTripBudget)}</p>"],
    [/>\$\{trip\.budget\}<\/td>/g, ">{formatCurrency(trip.budget || 0)}</td>"]
]);

