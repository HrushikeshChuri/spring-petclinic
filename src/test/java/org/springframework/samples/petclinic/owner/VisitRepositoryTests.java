/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class VisitRepositoryTests {

	@Autowired
	private VisitRepository visits;

	@Autowired
	private OwnerRepository owners;

	@Test
	void returnsZeroWhenNoUpcomingVisits() {
		assertThat(visits.countByDateGreaterThanEqual(LocalDate.now())).isZero();
	}

	@Test
	@Transactional
	void countsVisitsOnOrAfterToday() {
		Optional<Owner> optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();
		Pet pet = owner.getPet(7);

		Visit yesterday = new Visit();
		yesterday.setDate(LocalDate.now().minusDays(1));
		yesterday.setDescription("past");
		owner.addVisit(pet.getId(), yesterday);

		Visit today = new Visit();
		today.setDate(LocalDate.now());
		today.setDescription("today");
		owner.addVisit(pet.getId(), today);

		Visit tomorrow = new Visit();
		tomorrow.setDate(LocalDate.now().plusDays(1));
		tomorrow.setDescription("tomorrow");
		owner.addVisit(pet.getId(), tomorrow);

		owners.save(owner);

		assertThat(visits.countByDateGreaterThanEqual(LocalDate.now())).isEqualTo(2);
	}

}
