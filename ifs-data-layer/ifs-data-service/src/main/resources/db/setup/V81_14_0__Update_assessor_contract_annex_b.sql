SELECT @currentcontractid := id FROM contract where current;

UPDATE contract SET annex_b = '<h2 class="heading-medium">
  Travel and subsistence rates for non-civil service contracted personnel
</h2>

<table>
  <thead>
    <tr>
      <th scope="col">Day subsistence</th>
      <th scope="col" class="numeric">Rate</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Breakfast (only if leaving home before 6:30am)</td>
      <td class="numeric">&pound;10</td>
    </tr>
    <tr>
      <td>Day subsistence: Lunch (only if lunch is not already provided)</td>
      <td class="numeric">&pound;8</td>
    </tr>
    <tr>
      <td>Dinner (only if returning home after 8:00pm)</td>
      <td class="numeric">&pound;30</td>
    </tr>
  </tbody>
</table>

<h2 class="heading-medium">
  24 hour / overnight subsistence
</h2>

<p>If you need to book overnight accommodation for assessor events, hotel receipts can be claimed up to the following values:</p>

<table>
  <thead>
    <tr>
      <th scope="col">24 hour / overnight subsistence</th>
      <th scope="col" class="numeric">Rate</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>London (Bed and Breakfast)</td>
      <td class="numeric">&pound;170</td>
    </tr>
    <tr>
      <td>Elsewhere in the UK (Bed and Breakfast)</td>
      <td class="numeric">&pound;125</td>
    </tr>
  </tbody>
</table>

<p class="extra-margin-top">The table below shows the maximum amount of subsistence payable for meals and refreshments which includes an overnight stay.  All expenditure must be fully supported by receipts.</p>

 <table>
  <thead>
    <tr>
      <th scope="col">Meals and refreshments</th>
      <th scope="col" class="numeric">Rate</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Breakfast (if it is not included in the hotel rate)</td>
      <td class="numeric">&pound;10</td>
    </tr>
    <tr>
      <td>Lunch (only if lunch is not already provided)</td>
      <td class="numeric">&pound;8</td>
    </tr>
    <tr>
      <td>Dinner (only if returning home after 8pm)</td>
      <td class="numeric">&pound;30</td>
    </tr>
    <tr>
      <td>Drinks.  Maximum 3 drinks per day</td>
      <td class="numeric">&pound;2 per drink</td>
    </tr>
  </tbody>
</table>

<h2 class="heading-medium">
  Public transport
</h2>

<p>Standard class train travel should be used where appropriate. Air travel should be agreed with your Innovate UK Delivery Manager in advance.</p>

<p>Please confirm any significant travel costs with Innovate UK before you book. We may be able to pre-book travel on your behalf.</p>

<h2 class="heading-medium">
  Mileage rates
</h2>

<p>(These rates eliminate any individual tax/NIC liability)</p>

<dl class="standard-definition-list extra-margin-bottom">
  <dt>Up to 10,000 miles</dt>
  <dd>45p (flat rate not dependent on car engine size)</dd>
</dl>

<p class="extra-margin">
  <strong>Please make sure your travel claims, receipts and tickets are all submitted.</strong>
</p>' WHERE `id` = @currentcontractid;