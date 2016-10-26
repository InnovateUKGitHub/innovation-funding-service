SELECT @currentcontractid := id FROM contract where current;

UPDATE contract SET annex_a = '<h2 class="heading-medium">
Technology programme: Assessor services
</h2>

<ol class="list-number">
<li>The <strong>Contracting Authority</strong> requires that certain Projects in respect of which Grant Offers have been made under the Technology Programme shall on their completion be evaluated and reports made as to their outcomes and performance.</li>
<li><strong>The Contractor</strong> shall undertake Reviewing Services with respect to projects assigned to them from time to time by the <strong>Contracting Authority</strong>.</li>
<li>The procedures and guidelines for this Review are Contracting Authority Reviewers Guidance (or other processes as instructed by the Contracting Authority’s Programme Manager) as were presented to the Contractor during their initial Reviewing Day.  Any future/further requirements or changes shall be agreed between the Contractor and the Contracting Authority’s Delivery Manager and documented accordingly.</li>
</ol>

<h2 class="heading-medium">
For the avoidance of doubt
</h2>

<ol class="list-number" start="4">
	<li>The Contractor shall not be and shall not represent himself/herself to be an employee of the Contracting Authority or any Secretary of State of Her Majesty’s Government.  The Contractor is not empowered to vary any Term or Condition of the Project’s grant offer letter or any other provision of the Project or any other contracts nor shall the Contractor authorise any payment (or refund) by the Contracting Authority or any Secretary of State without formal written agreement of the Contracting Authority’s Delivery Manager.</li>
	<li>Under the terms of this Contract the Contractor is an agent of the Contracting Authority and as such he/she is commissioned only to provide the Services only to the Contracting Authority and, where appropriate and when specifically instructed, other organisations as directed to by the Contracting Authority.</li>
	<li>The Contract does not provide the Contractor with authority to instruct, make recommendation, guide or in any way to influence the Project which is the subject of the Services, or any participant, collaborator or agent in the Project or any other Project, in the delivery of their Project(s).</li>
	<li>Nothing in these conditions or in any part of the Contract shall impose any liability on any member of the staff of the Contracting Authority or its representatives in their Official or Personal capacity.</li>
</ol>' WHERE `id` = @currentcontractid;

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
    		<td>Breakfast (only if leaving home before 6.30am)</td>
    		<td class="numeric">&pound;10</td>
    	</tr>
    	<tr>
    		<td>Day Subsistence – Lunch (only if lunch is not already provided)</td>
    		<td class="numeric">&pound;8</td>
    	</tr>
    	<tr>
    		<td>Dinner (only if returning home after 8.00pm)</td>
    		<td class="numeric">&pound;30</td>
    	</tr>
</tbody>
</table>


<h2 class="heading-medium">
24 hour / overnight subsistence
</h2>

<p>Where it is necessary to book overnight accommodation in order to attend an assessor workshop, hotel receipts may be claimed up to the following value:</p>

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
    		<td>Breakfast (where not included in the hotel rate)</td>
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

<p>Standard class train travel should be used where appropriate. Air travel should be agreed with Innovate UK Delivery Manager in advance.</p>

<p>Where travel costs are likely to be significant, please liaise with the Technology Strategy Board in advance of booking as we may be able to help by pre-booking travel on your behalf.  Please contact us for more information.</p>

<h2 class="heading-medium">
Mileage rates
</h2>

<p>(These rates eliminate any individual tax/NIC liability)</p>

<dl class="standard-definition-list extra-margin-bottom">
<dt>Up to 10,000 miles</dt>
<dd>45p (flat rate -not dependent on car engine size)</dd>
</dl>

<p class="extra-margin">
<strong>Please ensure that with all your travel claims, receipts and tickets are submitted. </strong>
</p>' WHERE `id` = @currentcontractid;

UPDATE contract SET annex_c = '<h2 class="heading-medium">
	    	Information management
	    </h2>

	    <h2 class="heading-medium">
	    	General provisions
	    </h2>


	    <h3 class="heading-small">Ownership</h3>

	    <ol class="list-number">
	    	<li>
	    		The Contracting Authority shall at all times own the Information shared under this Contract, together with any results or works generated using such Information insofar as the Information can’t be separated or otherwise contains Personal Data or Confidential Information.
	    	</li>
	    </ol>

	    <h3 class="heading-small">Quality</h3>

	    <ol class="list-number" start="2">
	    	<li>
	    		While the Information provided and shared by Contracting Authority is believed to be reliable, Contracting Authority makes no representation or warranty as to its accuracy or completeness.
	    	</li>
	    </ol>

	    <h3 class="heading-small">Shared</h3>

	    <ol class="list-number" start="3">
	    	<li>The Information agreed to be shared by Contracting Authority with the Contractor is detailed in Annex A.</li>
	    	<li>The timing and method(s) of transfer are to be agreed between the parties, with both parties using their reasonable endeavours to complete the agreed transfer within a reasonable amount of time.</li>
	    </ol>

	    <h3 class="heading-small">Security</h3>

	    <ol class="list-number" start="5">
	    	<li>The Contractor shall ensure a level of security in relation to its storage and use of Information that is reasonable and that is appropriate to the harm that might result from a breach.</li>
	    </ol>

	    <h3 class="heading-small">Return of information</h3>

	    <ol class="list-number" start="6">
	    	<li>At the request of the Contracting Authority, or automatically upon the termination of this Contract, the Contractor shall promptly return or, at the Contracting Authority’s option, destroy all documents and materials and any copies containing, reflecting, incorporating or based on the Information and certify in writing that it has complied with the requirements of this condition.</li>
	    	<li>The Contractor shall ensure that any disposal, deletion or destruction of Information shall be secure and render the information being disposed of, deleted or destroyed beyond use.</li>
	    </ol>

	    <h2 class="heading-medium">
	    	Confidential Information
	    </h2>

	    <h3 class="heading-small">Definition</h3>

	     <ol class="list-number" start="8">
			<li>Confidential Information, for the purpose of this Contract, means information, including any information or analysis derived therefrom, howsoever obtained under this Contract relating to, without limitation, the business, products, affairs, strategy and finances of the relevant party for the time being confidential to the relevant party and trade secrets including, without limitation, technical data and know-how, relating to the business of the relevant party or any of its staff, management, clients, customers, visitors, partners, suppliers, agents, or distributors. This information specifically includes

				<ol class="list-number list-style-type-roman">
					<li>information belonging to third parties pursuant to which the disclosing party has a duty of confidentiality; and </li>
					<li>information listed in the Schedules and Annexes as confidential.  </li>
				</ol>

				<h4 class="heading-small">This information however does not include:</h4>

				<ol class="list-number list-style-type-roman">

					<li>any information which is already in, or comes into, the public domain otherwise than through a breach of this Contract;</li>
					<li>any information which was in the possession of the receiving Party, without restriction as to its disclosure, before receiving it from the disclosing Party, or</li>
					<li>which is received from a third party who lawfully acquired it and who is under no obligation restricting its disclosure;</li>
					<li>is independently developed without access to the Confidential Information as evidenced by receiving Party’s written notes; or</li>
					<li>which must be disclosed pursuant to a statutory, legal or parliamentary obligation placed upon the Party making the disclosure, including (without limitation) any requirements for disclosure under the Freedom of Information Act 2000.</li>

				</ol>

			</li>
	     </ol>


	     <h3 class="heading-small">Processing of confidential information</h3>

	     <ol class="list-number" start="9">
			<li>The Contractor shall keep the Contracting Authority’s Confidential Information confidential and, except with the prior written consent of the Contracting Authority, shall not use or exploit the Confidential Information in any way except for the express purpose of providing the Services.</li>
			<li>The Contractor shall not disclose or make available the Confidential Information in whole or in part to any third party, except as expressly permitted by the Contract.</li>
			<li>The Contractor shall not copy, reduce to writing or otherwise record the Confidential Information except as strictly necessary for providing the Services.</li>
			<li>The Contractor shall not seek to make contact with any third party that is the subject of the Contracting Authority’s Confidential Information, unless expressly permitted under this Contract.</li>
			<li>The Contractor may disclose Confidential Information to the extent required by law by any government or other regulatory authority, provided that, to the extent it is legally permitted to do so, it gives the Contracting Authority as much notice as reasonably possible and acts reasonably upon any reasonable request by the Contracting Authority in relation to such disclosures.</li>

	     </ol>


		<h2 class="heading-medium">
	    	Personal data
	    </h2>

	    <h3 class="heading-small">Definition</h3>

	    <ol class="list-number" start="14">
			<li>For the purpose of this Contract, Personal Data shall have the same meaning as set out in the Data Protection Act 1998.</li>
	    </ol>

	    <h3 class="heading-small">Relationship</h3>

	    <ol class="list-number" start="15">
			<li>The parties acknowledge and agree that for the purposes of the Data Protection Act 1998, the Contracting Authority is the Data Controller and the Contractor is the Data Processor.</li>
	    </ol>

	    <h3 class="heading-small">Processing of personal data</h3>

	    <ol class="list-number" start="16">
			<li>The Contractor shall Process Personal Data in accordance with the Data Protection Act 1998, and all other applicable data protection legislation, and only for the strict purpose of providing the Services.</li>
			<li>The Contractor shall not disclose the Personal Data to any third party other than at the request of Contracting Authority or as provided for in this Contract.</li>
			<li>Any Personal Data which arises out of the processing activities of the Contractor shall belong to and be controlled by the Contracting Authority, and may be processed by the Contractor in accordance with this Contract.</li>
			<li>The Contractor shall fully indemnify and hold harmless the Contracting Authority, its employees and agents against all liabilities, losses, costs, charges and expenses incurred as a result of any claims, demands, actions and proceedings made or brought against the Contracting Authority by any person arising from the loss, unauthorised disclosure of Personal Data by the Contractor, or any sub-contractor, servant or agent of the Contractor or any person within the control of the Contractor.</li>
			<li>Subject to Condition 19, the Contractor shall at its own expense conduct any litigation arising from any such claims, demands, actions or proceedings and all the negotiations for the settlement of the same, and the Contracting Authority hereby agrees to grant the Contractor exclusive control of any such litigation or the negotiations for the settlement of the same.</li>
	    </ol>

	    <h3 class="heading-small">Security</h3>

	    <ol class="list-number" start="21">
			<li>The Contractor shall take appropriate technical and administrative measures against the unauthorised or unlawful processing of Personal Data and against the accidental loss or destruction of, or damage to, Personal Data.</li>
	    </ol>

	    <h3 class="heading-small">Requests</h3>

	    <ol class="list-number" start="22">
			<li>The Contractor shall promptly comply with any request from Contracting Authority requiring it to amend, transfer or delete the Personal Data.</li>
			<li>If the Contractor receives any request, complaint, notice or communication which relates directly or indirectly to the processing of the Personal Data, it shall immediately (and no later than two working days) notify Contracting Authority’s Information Officer and provide Contracting Authority with full co-operation and assistance in relation to any such complaint, notice or communication.</li>
			<li>The Contractor shall notify the Contracting Authority immediately if it becomes aware of any unauthorised or unlawful processing, loss of, damage to or destruction of the Personal Data.</li>
	    </ol>' WHERE `id` = @currentcontractid;