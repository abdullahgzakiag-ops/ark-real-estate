/**
 * ARK Real Estate Pakistan — Interactive Web Application
 * Conforming strictly with AGENTS.md product decisions & Stitch designs.
 */

// Global State
const state = {
  activeTab: 'marketplace',
  language: 'en', // 'en' or 'ur'
  userRole: 'OWNER_AGENT', // 'BUYER_RENTER' | 'OWNER_AGENT' | 'PROPERTY_MANAGER' | 'ADMIN'
  activePurpose: 'ALL', // 'ALL' | 'FOR_SALE' | 'FOR_RENT'
  activeCity: 'ALL', // 'ALL' | 'Lahore' | 'Karachi' | 'Islamabad' | 'Rawalpindi'
  searchQuery: '',
  savedListingIds: new Set(['listing-1', 'listing-3']),
  listings: [
    {
      id: 'listing-1',
      title: '1 Kanal Luxury Designer Villa',
      titleUrdu: '1 کنال پرتعیش ڈیزائنر ولا',
      location: 'DHA Phase 5, Sector G',
      city: 'Lahore',
      pricePkr: 85000000,
      priceDisplay: 'PKR 8.5 Crore',
      priceDisplayUrdu: '8.5 کروڑ روپے',
      purpose: 'FOR_SALE',
      propertyType: 'HOUSE',
      areaSize: 1,
      areaUnit: 'KANAL',
      bedrooms: 5,
      bathrooms: 6,
      isVerified: true,
      status: 'APPROVED',
      image: 'https://images.unsplash.com/photo-1613490493576-7fde63acd811?auto=format&fit=crop&w=800&q=80',
      description: 'Ultra-modern Spanish architect architecture featuring imported Italian tile flooring, double-height lobby, German fittings, and lush landscaped patio.',
      owner: { name: 'Chaudhry Tariq', phone: '+92 300 8472911', agency: 'Apex Elite Estates' }
    },
    {
      id: 'listing-2',
      title: '500 Sq Yd Modern Corner Villa',
      titleUrdu: '500 مربع گز جدید کارنر ولا',
      location: 'Block 4, Clifton Marine Drive',
      city: 'Karachi',
      pricePkr: 145000000,
      priceDisplay: 'PKR 14.5 Crore',
      priceDisplayUrdu: '14.5 کروڑ روپے',
      purpose: 'FOR_SALE',
      propertyType: 'HOUSE',
      areaSize: 500,
      areaUnit: 'SQ_YD',
      bedrooms: 6,
      bathrooms: 7,
      isVerified: true,
      status: 'APPROVED',
      image: 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80',
      description: 'Exclusive seaside residence with secure perimeter, swimming pool, dual servant quarters, and standby solar power setup.',
      owner: { name: 'Mian Asad', phone: '+92 321 4455667', agency: 'Karachi Prime Realty' }
    },
    {
      id: 'listing-3',
      title: '2 Kanal Diplomatic Enclave Residence',
      titleUrdu: '2 کنال سفارتی انکلیو رہائش گاہ',
      location: 'Sector F-7/2, Margalla Road',
      city: 'Islamabad',
      pricePkr: 220000000,
      priceDisplay: 'PKR 22.0 Crore',
      priceDisplayUrdu: '22 کروڑ روپے',
      purpose: 'FOR_SALE',
      propertyType: 'HOUSE',
      areaSize: 2,
      areaUnit: 'KANAL',
      bedrooms: 6,
      bathrooms: 7,
      isVerified: true,
      status: 'APPROVED',
      image: 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80',
      description: 'Prestigious Margalla facing mansion with bulletproof perimeter, private screening theatre, smart home automation, and heated pool.',
      owner: { name: 'Sardar Usman', phone: '+92 333 9988776', agency: 'Capital Horizon Real Estate' }
    },
    {
      id: 'listing-4',
      title: '10 Marla Executive Rental Portion',
      titleUrdu: '10 مرلہ ایگزیکٹو کرایہ پورشن',
      location: 'Bahria Town Phase 7, Safari Valley',
      city: 'Rawalpindi',
      pricePkr: 165000,
      priceDisplay: 'PKR 1.65 Lakh / mo',
      priceDisplayUrdu: '1.65 لاکھ روپے / ماہانہ',
      purpose: 'FOR_RENT',
      propertyType: 'UPPER_PORTION',
      areaSize: 10,
      areaUnit: 'MARLA',
      bedrooms: 3,
      bathrooms: 3,
      isVerified: true,
      status: 'APPROVED',
      image: 'https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=800&q=80',
      description: 'Brand new luxury portion with independent entrance, contemporary kitchen with imported appliances, and secure covered garage.',
      owner: { name: 'Malik Naveed', phone: '+92 300 1122334', agency: 'Bahria Heights Associates' }
    },
    {
      id: 'listing-5',
      title: 'Luxury 3-Bed Serviced Penthouse',
      titleUrdu: 'پرتعیش 3 بیڈ سروسڈ پینٹ ہاؤس',
      location: 'Gulberg III, MM Alam Boulevard',
      city: 'Lahore',
      pricePkr: 280000,
      priceDisplay: 'PKR 2.80 Lakh / mo',
      priceDisplayUrdu: '2.80 لاکھ روپے / ماہانہ',
      purpose: 'FOR_RENT',
      propertyType: 'PENTHOUSE',
      areaSize: 3200,
      areaUnit: 'SQ_FT',
      bedrooms: 3,
      bathrooms: 4,
      isVerified: true,
      status: 'APPROVED',
      image: 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80',
      description: 'Panoramic skyline views, private terrace with jacuzzi, 24/7 dedicated security concierge, and dual basement parking bays.',
      owner: { name: 'Dr. Zeeshan Haider', phone: '+92 345 6677889', agency: 'Direct Landlord' }
    },
    {
      id: 'listing-6',
      title: '1 Kanal Royal Classic Villa (Pending Vetting)',
      titleUrdu: '1 کنال رائل ولا (زیرِ تصدیق)',
      location: 'DHA Phase 6, Sector C',
      city: 'Lahore',
      pricePkr: 92000000,
      priceDisplay: 'PKR 9.2 Crore',
      priceDisplayUrdu: '9.2 کروڑ روپے',
      purpose: 'FOR_SALE',
      propertyType: 'HOUSE',
      areaSize: 1,
      areaUnit: 'KANAL',
      bedrooms: 5,
      bathrooms: 6,
      isVerified: false,
      status: 'PENDING_APPROVAL',
      image: 'https://images.unsplash.com/photo-1580587771525-78b9dba3b914?auto=format&fit=crop&w=800&q=80',
      description: 'Newly finished architect designed home. Title deeds submitted to ARK custodian for LDA registry verification.',
      owner: { name: 'Farhan Qureshi', phone: '+92 301 5544332', agency: 'Qureshi Estates' }
    }
  ],
  managementData: {
    portfolioHealth: {
      occupancyRate: '94.2%',
      monthlyRentRollPkr: 'PKR 34.5 Lakh',
      activeTenancies: 18,
      pendingMaintenance: 2,
      realizedNetIncome: 'PKR 3,022,200',
      noiPercentage: '87.6%'
    },
    units: [
      { id: 'u1', name: 'Villa 12-A', type: '1 Kanal Villa', tenant: 'Bilal Ahmed (CNIC: 35202-***-1)', rentPkr: 220000, status: 'PAID', lastPayment: '02 Oct 2026' },
      { id: 'u2', name: 'Penthouse B', type: '3-Bed Penthouse', tenant: 'Hamza Khan (CNIC: 37405-***-5)', rentPkr: 280000, status: 'PAID', lastPayment: '04 Oct 2026' },
      { id: 'u3', name: 'Upper Portion 44', type: '10 Marla Portion', tenant: 'Kamran Raza (CNIC: 35201-***-9)', rentPkr: 165000, status: 'DUE', lastPayment: 'Pending Log' },
      { id: 'u4', name: 'Commercial Shop 8', type: 'Retail Unit', tenant: 'Al-Madina Grocers (CNIC: 35202-***-3)', rentPkr: 95000, status: 'OVERDUE', lastPayment: '18 Aug 2026' }
    ],
    expenses: [
      { category: 'Security & Guard Force', amountPkr: 'PKR 145,000', society: 'DHA Society Dues' },
      { category: 'Preventative HVAC Maintenance', amountPkr: 'PKR 85,000', vendor: 'CoolPoint Tech' },
      { category: 'Provincial Property Taxes', amountPkr: 'PKR 120,000', authority: 'Excise & Taxation Punjab' },
      { category: 'ARK Property Management Fee', amountPkr: 'PKR 77,800', note: 'Standard Custodian Care' }
    ]
  }
};

// Bilingual Strings Catalog
const i18n = {
  en: {
    tagline: "PAKISTAN'S TRUSTED REAL ESTATE MARKETPLACE & MANAGEMENT",
    verifiedAccess: 'CDA & LDA Compliant Property Registry',
    searchPlaceholder: 'Search DHA, Bahria Town, Gulberg, Clifton...',
    allCities: 'All Pakistan',
    allPurpose: 'All Purpose',
    buy: 'Buy (Sale)',
    rent: 'Rent',
    verifiedDirect: 'Verified Direct',
    inquireViewing: 'Inquire / Book Viewing',
    viewDetails: 'View Details',
    submitListing: 'Submit Property Listing',
    myListings: 'My Listings',
    savedProperties: 'Saved',
    moderation: 'Admin Moderation',
    portfolio: 'Portfolio Management',
    financialReport: 'Financial Reports',
    accountSettings: 'Account Settings',
    custodianNotice: 'ARK Custodian Vetting Notice',
    custodianNoticeDesc: 'All submissions are held in PENDING status until ownership documentation is verified by an administrator. ARK strictly complies with Rule 17 & 19.',
    rule18Notice: 'Payment Record Notice (Rule 18)',
    rule18NoticeDesc: 'Payment logged manually. Subject to verification by ARK custodian before clearance. ARK does not transfer or process funds.',
    realizedIncome: 'Realized Net Income (PKR)',
    noiRate: 'Net Operating Income (NOI)',
    recordPayment: 'Record Manual Payment',
    submitForVetting: 'Submit for Admin Vetting',
    approveBadge: 'Approve & Grant Verified Badge',
    rejectListing: 'Reject / Request Revisions',
    langToggle: 'اردو (RTL)'
  },
  ur: {
    tagline: 'پاکستان کی بااعتماد رئیل اسٹیٹ مارکیٹ اور مینجمنٹ',
    verifiedAccess: 'سی ڈی اے اور ایل ڈی اے کے مطابق مصدقہ رجسٹری',
    searchPlaceholder: 'ڈی ایچ اے، بحریہ ٹاؤن، گلبرگ، کلفٹن میں تلاش کریں...',
    allCities: 'تمام پاکستان',
    allPurpose: 'تمام مقاصد',
    buy: 'برائے فروخت',
    rent: 'برائے کرایہ',
    verifiedDirect: 'تصدیق شدہ لسٹنگ',
    inquireViewing: 'رابطہ / معائنہ بک کریں',
    viewDetails: 'تفصیلات دیکھیں',
    submitListing: 'پراپرٹی لسٹنگ جمع کروائیں',
    myListings: 'میری لسٹنگز',
    savedProperties: 'پسندیدہ',
    moderation: 'ایڈمن جائزہ و تصدیق',
    portfolio: 'پورٹ فولیو مینجمنٹ',
    financialReport: 'مالیاتی رپورٹ اور اخراجات',
    accountSettings: 'اکاؤنٹ اور ترتیبات',
    custodianNotice: 'آرک کسٹوڈین تصدیقی نوٹس',
    custodianNoticeDesc: 'تمام لسٹنگز پہلے تصدیقی مرحلے میں رکھی جاتی ہیں تاکہ ملکیت کے دستاویزات کی جانچ کی جا سکے۔ آرک اصول 17 اور 19 کی مکمل پاسداری کرتا ہے۔',
    rule18Notice: 'ادائیگی اندراج نوٹس (اصول 18)',
    rule18NoticeDesc: 'ادائیگی دستی طور پر درج کی گئی۔ کلیئرنس سے قبل تصدیق ضروری ہے۔ آرک کوئی رقم منتقل نہیں کرتا۔',
    realizedIncome: 'خالص وصول شدہ آمدنی (روپے)',
    noiRate: 'نیٹ آپریٹنگ انکم (NOI)',
    recordPayment: 'دستی ادائیگی درج کریں',
    submitForVetting: 'تصدیق کے لیے جمع کروائیں',
    approveBadge: 'منظور اور تصدیقی بیج جاری کریں',
    rejectListing: 'مسترد / ترمیم کی درخواست کریں',
    langToggle: 'English (LTR)'
  }
};

// UI Initializer
document.addEventListener('DOMContentLoaded', () => {
  initHealthCheck();
  renderNavigation();
  renderMarketplace();
  renderManagement();
  renderModeration();
  renderFinancials();
  bindEvents();
});

// Live Health Check Ping
async function initHealthCheck() {
  const chip = document.getElementById('healthChip');
  if (!chip) return;

  try {
    const res = await fetch('/v1/health');
    const data = await res.json();
    if (data.success) {
      chip.innerHTML = `
        <span class="pulse-dot"></span>
        <span>API Live (${data.data.database.provider})</span>
      `;
    }
  } catch (err) {
    chip.innerHTML = `
      <span class="pulse-dot" style="background:#eab308;"></span>
      <span>Local Dev Mode (Cached)</span>
    `;
  }
}

// Navigation & Tab Switching
function switchTab(tabId) {
  state.activeTab = tabId;
  document.querySelectorAll('.view-panel').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-tab-btn').forEach(b => b.classList.remove('active'));

  const targetPanel = document.getElementById(`panel-${tabId}`);
  const targetBtn = document.getElementById(`tab-btn-${tabId}`);
  if (targetPanel) targetPanel.classList.add('active');
  if (targetBtn) targetBtn.classList.add('active');

  // Trigger any required refreshes
  if (tabId === 'moderation') renderModeration();
  if (tabId === 'management') renderManagement();
  if (tabId === 'marketplace') renderMarketplace();
  if (tabId === 'financials') renderFinancials();
}

// Render Marketplace Grid
function renderMarketplace() {
  const container = document.getElementById('marketplaceGrid');
  if (!container) return;

  const t = i18n[state.language];

  // Filter listings
  const filtered = state.listings.filter(item => {
    // Rule 17 & 19: Only APPROVED listings visible on public marketplace
    if (item.status !== 'APPROVED') return false;

    if (state.activePurpose !== 'ALL' && item.purpose !== state.activePurpose) return false;
    if (state.activeCity !== 'ALL' && item.city !== state.activeCity) return false;

    if (state.searchQuery) {
      const q = state.searchQuery.toLowerCase();
      const matchTitle = item.title.toLowerCase().includes(q);
      const matchLoc = item.location.toLowerCase().includes(q);
      const matchCity = item.city.toLowerCase().includes(q);
      if (!matchTitle && !matchLoc && !matchCity) return false;
    }
    return true;
  });

  if (filtered.length === 0) {
    container.innerHTML = `
      <div style="grid-column: 1 / -1; text-align: center; padding: 60px 20px; background: white; border-radius: 16px; border: 1px dashed var(--surface-border);">
        <span class="material-symbols-outlined" style="font-size: 48px; color: var(--text-muted); margin-bottom: 12px;">search_off</span>
        <h3 style="font-size: 1.2rem; font-weight: 700; color: var(--primary); margin-bottom: 6px;">No Verified Listings Found</h3>
        <p style="color: var(--text-muted); font-size: 0.9rem;">Try selecting a different city, adjusting your search keywords, or clearing your purpose filter.</p>
        <button class="btn-secondary" style="margin-top: 16px;" onclick="resetFilters()">Reset All Filters</button>
      </div>
    `;
    return;
  }

  container.innerHTML = filtered.map(item => {
    const isSaved = state.savedListingIds.has(item.id);
    const title = state.language === 'ur' ? (item.titleUrdu || item.title) : item.title;
    const price = state.language === 'ur' ? (item.priceDisplayUrdu || item.priceDisplay) : item.priceDisplay;

    return `
      <article class="property-card" id="card-${item.id}">
        <div class="card-image-wrap">
          <img src="${item.image}" alt="${title}" loading="lazy" />
          <div class="card-badges-row">
            <span class="badge-purpose ${item.purpose === 'FOR_SALE' ? 'sale' : 'rent'}">
              ${item.purpose === 'FOR_SALE' ? t.buy : t.rent}
            </span>
            <div style="display: flex; gap: 8px;">
              ${item.isVerified ? `
                <span class="badge-verified">
                  <span class="material-symbols-outlined text-[16px]" style="font-variation-settings: 'FILL' 1;">verified</span>
                  ${t.verifiedDirect}
                </span>
              ` : ''}
              <button class="btn-favorite-heart ${isSaved ? 'active' : ''}" onclick="toggleSaveListing('${item.id}', event)" title="Save Property">
                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' ${isSaved ? 1 : 0};">favorite</span>
              </button>
            </div>
          </div>
        </div>
        <div class="card-body">
          <div class="price-row">
            <div class="price-amount">${price}</div>
            <div class="price-denomination">${item.areaSize} ${item.areaUnit}</div>
          </div>
          <h3 class="property-title">${title}</h3>
          <div class="property-location">
            <span class="material-symbols-outlined text-[16px]">location_on</span>
            <span>${item.location}, ${item.city}</span>
          </div>
          <div class="property-specs-grid">
            <div class="spec-item">
              <span class="material-symbols-outlined">bed</span>
              <span>${item.bedrooms} Beds</span>
            </div>
            <div class="spec-item">
              <span class="material-symbols-outlined">bathtub</span>
              <span>${item.bathrooms} Baths</span>
            </div>
            <div class="spec-item">
              <span class="material-symbols-outlined">square_foot</span>
              <span>${item.areaSize} ${item.areaUnit}</span>
            </div>
          </div>
          <div class="card-actions">
            <button class="btn-primary" style="flex: 1;" onclick="openViewingModal('${item.id}')">
              <span class="material-symbols-outlined text-[18px]">calendar_today</span>
              <span>${t.inquireViewing}</span>
            </button>
            <button class="btn-secondary" onclick="openDetailsModal('${item.id}')">
              <span class="material-symbols-outlined text-[18px]">visibility</span>
            </button>
          </div>
        </div>
      </article>
    `;
  }).join('');
}

// Render Admin Moderation View
function renderModeration() {
  const container = document.getElementById('moderationList');
  if (!container) return;

  const t = i18n[state.language];
  const pending = state.listings.filter(i => i.status === 'PENDING_APPROVAL');

  const counter = document.getElementById('moderationCount');
  if (counter) counter.innerText = pending.length;

  if (pending.length === 0) {
    container.innerHTML = `
      <div style="text-align: center; padding: 48px; background: white; border-radius: 16px; border: 1px solid var(--surface-border);">
        <span class="material-symbols-outlined" style="font-size: 48px; color: var(--secondary); margin-bottom: 12px;">task_alt</span>
        <h3 style="font-size: 1.2rem; font-weight: 700; color: var(--primary);">All Listings Moderated</h3>
        <p style="color: var(--text-muted); font-size: 0.9rem;">There are currently zero pending listings in the administrator review queue.</p>
      </div>
    `;
    return;
  }

  container.innerHTML = pending.map(item => `
    <div class="property-card" style="margin-bottom: 20px; border-left: 6px solid var(--accent-gold);">
      <div style="display: flex; gap: 20px; padding: 20px; flex-wrap: wrap;">
        <img src="${item.image}" alt="${item.title}" style="width: 220px; height: 160px; object-fit: cover; border-radius: 12px;" />
        <div style="flex: 1; min-width: 280px;">
          <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 8px;">
            <div>
              <span class="status-chip pending" style="margin-bottom: 6px;">PENDING ADMIN VETTING</span>
              <h3 style="font-size: 1.15rem; font-weight: 800; color: var(--primary);">${item.title}</h3>
              <p style="font-size: 0.85rem; color: var(--text-muted);">${item.location}, ${item.city}</p>
            </div>
            <div style="text-align: right;">
              <div style="font-size: 1.25rem; font-weight: 800; color: var(--primary);">${item.priceDisplay}</div>
              <div style="font-size: 0.8rem; color: var(--text-muted);">${item.areaSize} ${item.areaUnit} • ${item.bedrooms} Beds</div>
            </div>
          </div>
          <p style="font-size: 0.85rem; color: var(--text-main); margin-bottom: 12px; background: var(--surface-muted); padding: 10px; border-radius: 8px;">
            ${item.description}
          </p>
          <div style="font-size: 0.8rem; color: var(--text-muted); margin-bottom: 16px;">
            <strong>Seller:</strong> ${item.owner.name} (${item.owner.phone}) • <strong>Agency:</strong> ${item.owner.agency}
          </div>
          <div style="display: flex; gap: 12px; justify-content: flex-end;">
            <button class="btn-secondary" onclick="rejectListing('${item.id}')" style="color: #b91c1c; border-color: #fca5a5;">
              <span class="material-symbols-outlined text-[18px]">close</span>
              <span>${t.rejectListing}</span>
            </button>
            <button class="btn-success" onclick="approveListing('${item.id}')">
              <span class="material-symbols-outlined text-[18px]">verified</span>
              <span>${t.approveBadge}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  `).join('');
}

// Moderation Actions
function approveListing(id) {
  const item = state.listings.find(l => l.id === id);
  if (item) {
    item.status = 'APPROVED';
    item.isVerified = true;
    showToast(`✓ Listing "${item.title}" approved & granted Verified Badge! Now visible on public marketplace.`);
    renderModeration();
    renderMarketplace();
  }
}

function rejectListing(id) {
  const item = state.listings.find(l => l.id === id);
  if (item) {
    item.status = 'REJECTED';
    showToast(`Listing "${item.title}" marked as REJECTED. Revisions requested from seller.`);
    renderModeration();
  }
}

// Render Management Units
function renderManagement() {
  const tbody = document.getElementById('unitsTableBody');
  if (!tbody) return;

  tbody.innerHTML = state.managementData.units.map(u => `
    <tr>
      <td><strong>${u.name}</strong><br><span style="font-size: 0.78rem; color: var(--text-muted);">${u.type}</span></td>
      <td>${u.tenant}</td>
      <td><strong>PKR ${u.rentPkr.toLocaleString()}</strong></td>
      <td>
        <span class="status-chip ${u.status === 'PAID' ? 'approved' : u.status === 'DUE' ? 'pending' : 'rejected'}">
          ${u.status}
        </span>
      </td>
      <td>${u.lastPayment}</td>
      <td>
        <button class="btn-secondary" style="padding: 6px 12px; font-size: 0.78rem;" onclick="openPaymentModal('${u.name}', ${u.rentPkr})">
          <span class="material-symbols-outlined text-[16px]">receipt_long</span>
          <span>Log Payment</span>
        </button>
      </td>
    </tr>
  `).join('');
}

// Render Financials Breakdown
function renderFinancials() {
  const expContainer = document.getElementById('expensesList');
  if (!expContainer) return;

  expContainer.innerHTML = state.managementData.expenses.map(e => `
    <div style="display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; background: white; border-radius: 10px; border: 1px solid var(--surface-border); margin-bottom: 8px;">
      <div>
        <div style="font-weight: 700; color: var(--primary); font-size: 0.9rem;">${e.category}</div>
        <div style="font-size: 0.75rem; color: var(--text-muted);">${e.society || e.vendor || e.authority || e.note}</div>
      </div>
      <div style="font-weight: 800; color: var(--text-main); font-family: var(--font-display);">${e.amountPkr}</div>
    </div>
  `).join('');
}

// Toggle Save Property
function toggleSaveListing(id, evt) {
  if (evt) evt.stopPropagation();
  if (state.savedListingIds.has(id)) {
    state.savedListingIds.delete(id);
    showToast('Listing removed from saved properties');
  } else {
    state.savedListingIds.add(id);
    showToast('❤️ Listing saved to your favorites');
  }
  renderMarketplace();
  updateSavedBadge();
}

function updateSavedBadge() {
  const badge = document.getElementById('savedCountBadge');
  if (badge) badge.innerText = state.savedListingIds.size;
}

// Filter Actions
function setCityFilter(city) {
  state.activeCity = city;
  document.querySelectorAll('.city-chip').forEach(c => c.classList.remove('active'));
  const btn = document.getElementById(`city-chip-${city.toLowerCase()}`);
  if (btn) btn.classList.add('active');
  renderMarketplace();
}

function setPurposeFilter(purpose) {
  state.activePurpose = purpose;
  document.querySelectorAll('.purpose-btn').forEach(b => b.classList.remove('active'));
  const btn = document.getElementById(`purpose-btn-${purpose.toLowerCase()}`);
  if (btn) btn.classList.add('active');
  renderMarketplace();
}

function resetFilters() {
  state.activeCity = 'ALL';
  state.activePurpose = 'ALL';
  state.searchQuery = '';
  const searchInput = document.getElementById('searchInput');
  if (searchInput) searchInput.value = '';
  setCityFilter('ALL');
  setPurposeFilter('ALL');
}

// Submit New Listing
function handleNewListingSubmit(e) {
  e.preventDefault();
  const form = e.target;
  const title = form.elements['title'].value;
  const city = form.elements['city'].value;
  const location = form.elements['location'].value;
  const price = parseFloat(form.elements['pricePkr'].value);
  const purpose = form.elements['purpose'].value;
  const areaSize = parseFloat(form.elements['areaSize'].value);
  const areaUnit = form.elements['areaUnit'].value;
  const bedrooms = parseInt(form.elements['bedrooms'].value);
  const bathrooms = parseInt(form.elements['bathrooms'].value);
  const description = form.elements['description'].value;

  // Pakistani price display calculation
  let priceDisplay = `PKR ${price.toLocaleString()}`;
  if (price >= 10000000) {
    priceDisplay = `PKR ${(price / 10000000).toFixed(2)} Crore`;
  } else if (price >= 100000) {
    priceDisplay = `PKR ${(price / 100000).toFixed(2)} Lakh`;
  }

  const newListing = {
    id: `listing-${Date.now()}`,
    title,
    location,
    city,
    pricePkr: price,
    priceDisplay,
    purpose,
    propertyType: 'HOUSE',
    areaSize,
    areaUnit,
    bedrooms,
    bathrooms,
    isVerified: false,
    status: 'PENDING_APPROVAL', // Strict Rule 17 & 19 compliance
    image: 'https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=800&q=80',
    description,
    owner: { name: 'You (Owner)', phone: '+92 300 0000000', agency: 'Direct Submission' }
  };

  state.listings.unshift(newListing);
  form.reset();

  showToast(`📋 Listing submitted! Currently held in PENDING status for Admin Vetting (Rule 17/19).`);
  switchTab('moderation');
}

// Rule 18 Manual Payment Recording
function handleRecordPaymentSubmit(e) {
  e.preventDefault();
  const unit = document.getElementById('modalUnitName').innerText;
  const amount = document.getElementById('paymentAmountInput').value;
  const method = document.getElementById('paymentMethodSelect').value;
  const ref = document.getElementById('paymentRefInput').value;

  closeModal('paymentModal');

  showToast(`✅ Manual Payment of PKR ${amount} recorded for ${unit} via ${method} (Ref: ${ref}). Subject to custodian audit (Rule 18).`);

  // Update unit status
  const target = state.managementData.units.find(u => u.name === unit);
  if (target) {
    target.status = 'PAID';
    target.lastPayment = 'Today (Manual Log)';
    renderManagement();
  }
}

// Modal Handlers
function openPaymentModal(unitName, defaultAmount) {
  document.getElementById('modalUnitName').innerText = unitName;
  document.getElementById('paymentAmountInput').value = defaultAmount;
  openModal('paymentModal');
}

function openViewingModal(listingId) {
  const item = state.listings.find(l => l.id === listingId);
  if (item) {
    document.getElementById('viewingListingTitle').innerText = item.title;
    document.getElementById('viewingListingLocation').innerText = `${item.location}, ${item.city}`;
    openModal('viewingModal');
  }
}

function openDetailsModal(listingId) {
  const item = state.listings.find(l => l.id === listingId);
  if (!item) return;

  const content = document.getElementById('detailsModalContent');
  content.innerHTML = `
    <img src="${item.image}" alt="${item.title}" style="width: 100%; height: 260px; object-fit: cover; border-radius: 12px; margin-bottom: 16px;" />
    <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 8px;">
      <h3 style="font-size: 1.3rem; font-weight: 800; color: var(--primary);">${item.title}</h3>
      <div style="font-size: 1.3rem; font-weight: 800; color: var(--primary);">${item.priceDisplay}</div>
    </div>
    <p style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 14px;">
      <span class="material-symbols-outlined text-[16px]">location_on</span>
      ${item.location}, ${item.city}
    </p>
    <div style="background: var(--surface-muted); padding: 14px; border-radius: 10px; margin-bottom: 16px;">
      <h4 style="font-size: 0.85rem; font-weight: 700; color: var(--primary); margin-bottom: 6px;">ARCHITECTURAL & REGISTRY SPECIFICATIONS</h4>
      <p style="font-size: 0.88rem; color: var(--text-main);">${item.description}</p>
    </div>
    <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; margin-bottom: 16px; text-align: center;">
      <div style="background: var(--surface-card); border: 1px solid var(--surface-border); padding: 10px; border-radius: 8px;">
        <span class="material-symbols-outlined" style="color: var(--primary);">bed</span>
        <div style="font-weight: 700;">${item.bedrooms} Bedrooms</div>
      </div>
      <div style="background: var(--surface-card); border: 1px solid var(--surface-border); padding: 10px; border-radius: 8px;">
        <span class="material-symbols-outlined" style="color: var(--primary);">bathtub</span>
        <div style="font-weight: 700;">${item.bathrooms} Bathrooms</div>
      </div>
      <div style="background: var(--surface-card); border: 1px solid var(--surface-border); padding: 10px; border-radius: 8px;">
        <span class="material-symbols-outlined" style="color: var(--primary);">straighten</span>
        <div style="font-weight: 700;">${item.areaSize} ${item.areaUnit}</div>
      </div>
    </div>
    <div style="background: #f0fdf4; border: 1px solid #bbf7d0; padding: 12px; border-radius: 8px; font-size: 0.85rem; color: #166534; display: flex; align-items: center; gap: 8px;">
      <span class="material-symbols-outlined text-[20px]">verified_user</span>
      <span>Registry Status: CDA & LDA Verified Title Deed on record with ARK Custodian.</span>
    </div>
  `;
  openModal('detailsModal');
}

function openModal(id) {
  const m = document.getElementById(id);
  if (m) m.classList.add('active');
}

function closeModal(id) {
  const m = document.getElementById(id);
  if (m) m.classList.remove('active');
}

// Language & RTL Switching
function toggleLanguage() {
  state.language = state.language === 'en' ? 'ur' : 'en';
  const html = document.documentElement;
  html.setAttribute('lang', state.language);
  html.setAttribute('dir', state.language === 'ur' ? 'rtl' : 'ltr');

  const btn = document.getElementById('langToggleBtn');
  if (btn) btn.innerText = i18n[state.language].langToggle;

  renderNavigation();
  renderMarketplace();
  renderModeration();
  showToast(state.language === 'ur' ? 'اردو زبان اور دائیں سے بائیں (RTL) لے آؤٹ فعال ہو گیا' : 'English language (LTR) layout activated');
}

function renderNavigation() {
  const t = i18n[state.language];
  document.querySelectorAll('[data-i18n]').forEach(el => {
    const key = el.getAttribute('data-i18n');
    if (t[key]) el.innerText = t[key];
  });
}

// Toast Notifications Helper
function showToast(msg) {
  const container = document.getElementById('toastContainer');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = 'toast success';
  toast.innerHTML = `
    <span class="material-symbols-outlined text-[18px]">info</span>
    <span>${msg}</span>
  `;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

// Event Bindings
function bindEvents() {
  const searchInput = document.getElementById('searchInput');
  if (searchInput) {
    searchInput.addEventListener('input', (e) => {
      state.searchQuery = e.target.value;
      renderMarketplace();
    });
  }

  // Device shell toggler
  window.toggleDeviceShell = function(isMobile) {
    document.body.classList.toggle('mobile-shell-active', isMobile);
    document.getElementById('btn-desktop-view').classList.toggle('active', !isMobile);
    document.getElementById('btn-mobile-view').classList.toggle('active', isMobile);
  };
}
