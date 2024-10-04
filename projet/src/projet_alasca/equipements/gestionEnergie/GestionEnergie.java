package projet_alasca.equipements.gestionEnergie;

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.hem2024.bases.AdjustableCI;
import fr.sorbonne_u.components.hem2024.bases.RegistrationCI;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;

@RequiredInterfaces(required = {AdjustableCI.class, ElectricMeterCI.class,
		ClocksServerCI.class})
@OfferedInterfaces(offered={RegistrationCI.class})
public class GestionEnergie {

}
